package models

import scala.collection.immutable.WrappedString
import scala.collection.mutable.ListBuffer

case class Game() {

  def wordIsInvalid(word: GameWord): Boolean = word.isInvalid

  def guess(guesserId: Int, word: GameWord, guesseeId: Int): Option[Int] = {
    players(guesserId).addGuessedWord(word)
    players(guesseeId).secretWord.lettersInCommon(word)
  }

  private var players: ListBuffer[Player] = ListBuffer.empty

  def playerName(id: Int): String = players(id).name

  def playerGuesses(id: Int): Option[String] =
    players(id).getGuessedWords match {
      case Seq() => None
      case s => Some(s.toString)
    }

  def addPlayer(player: Player): Option[Int] = {
    if (player.isInvalid) None
    else {
      players += player
      Some(player.id)
    }
  }

}

object Game {

  private var game: Option[Game] = None

  private var gameState: GameState = AddPlayer(0)

  def getGameState: GameState = gameState

  def newGame: Option[Game] = {
    gameState = AddPlayer(0)
    game = Some(Game())
    game
  }

  def playerName(id: Int): String = game match {
    case None => "UNKNOWN_PLAYER"
    case Some(Game()) => game.get.playerName(id)
  }

  def playerGuesses(id: Int): Option[String] = getGame.playerGuesses(id)

  private def getGame: Game = game match {
    case None => newGame.get
    case Some(_) => game.get
  }

  def addPlayer(playerId: Int, name: String, secretWord: GameWord): GameState = {

    val nextGameState: GameState = playerId match {
      case 0 => AddPlayer(1)
      case 1 => NextPlayer(0)
      case _ => gameState
    }

    gameState match {
      case AddPlayer(n) =>
        if (playerId.equals(n)) {
          getGame.addPlayer(Player(n, name, secretWord: GameWord)) match {
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

  def guess(guesserId: Int, word: GameWord, guesseeId: Int): Option[Answer] = game match {
    case None =>
      newGame
      None

    case Some(game) =>
      if (game.wordIsInvalid(word)) None

      else if (gameState == NextPlayer(guesserId)) {
        val numberOfMatchingLetters: Option[Int] = getGame.guess(guesserId, word, guesseeId)

        numberOfMatchingLetters match {
          case None => None
          case Some(n: Int) =>
            if (n == GameWord.WORD_LENGTH)
              gameState = PlayerWon(guesserId)
            else {
              nextPlayer
            }

            Some(Answer(
              guesserId,
              n,
              gameState))
        }

      } else None
  }

}
