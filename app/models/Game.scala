package models

import scala.collection.immutable.WrappedString
import scala.collection.mutable.ListBuffer

case class Game() {

  private var gameState: GameState = AddPlayer(0)

  def getGameState: GameState = gameState

  def wordIsInvalid(word: Word): Boolean = word.isInvalid

  def guess(guesserId: Int, word: Word, guesseeId: Int): Option[Answer] = {
      if (wordIsInvalid(word))
        None
      else if (gameState == NextPlayer(guesserId)) {
        val numberOfMatchingLetters: Option[Int] = players(guesseeId).secretWord.lettersInCommon(word)

        numberOfMatchingLetters match {
          case None => None
          case Some(n: Int) =>
            if (word.value.equals(players(guesseeId).secretWord.value))
              gameState = PlayerWon(guesserId)
            else {
              nextPlayer
            }

            val answer: Answer = Answer(guesserId, word, n, gameState)
            players(guesserId).addAnswer(answer)
            Some(answer)
        }

      }
      else
        None
  }

  private var players: ListBuffer[Player] = ListBuffer.empty

  def playerName(id: Int): String = players(id).name

  def playerGuesses(id: Int): Option[String] =
    players(id).getGuessedWords match {
      case Seq() => None
      case s => Some(s.toString)
    }

  def playerAnswers(id: Int): Seq[Answer] = players(id).getAnswers

  def addPlayer(playerId: Int, name: String, secretWord: Word): GameState = {

    val nextGameState: GameState = playerId match {
      case 0 => AddPlayer(1)
      case 1 => NextPlayer(0)
      case _ => gameState
    }

    gameState match {
      case AddPlayer(n) =>
        if (playerId.equals(n)) {

          val player: Player = Player(n, name, secretWord: Word)

          val idOfAddedPlayer: Option[Int] = if (player.isInvalid || players.length >= 2)
            None
          else {
            players += player
            Some(player.id)
          }

          idOfAddedPlayer match {
            case None =>
            case Some(playerId: Int) =>
              if (playerId.equals(n))
                gameState = nextGameState
          }
        }
        gameState

      case _ => gameState
    }
  }


  def nextPlayer: Int = {
    gameState = gameState match {
      case NextPlayer(0) => NextPlayer(1)
      case NextPlayer(1) => NextPlayer(0)
      case _ => gameState
    }
    gameState.playerId
  }

}

