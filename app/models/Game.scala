package models

import scala.collection.immutable.WrappedString
import scala.collection.mutable.ListBuffer

case class Game() {

  def guess(word: String, guesseeId: Int): Int = {
    players(guesseeId)
      .secretWord
      .toSeq
      .intersect(word)
      .unwrap
      .length
  }

  private var players: ListBuffer[Player] = ListBuffer.empty

  def playerName(id: Int): String = players(id).name

  def addPlayer(player: Player): Int = {
    players += player
    player.id
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
    case None =>  "UNKNOWN_PLAYER"
    case Some(Game()) => game.get.playerName(id)
  }

  private def getGame: Game = game match {
    case None => newGame.get
    case Some(_) => game.get
  }

  def addPlayer(playerId: Int, name: String, secretWord: String): GameState = {
    gameState match {
      case AddPlayer(0) =>
        if (playerId == 0) {
          getGame.addPlayer(Player(0, name, secretWord))
          gameState = AddPlayer(1)
        }

      case AddPlayer(1) =>
        if (playerId == 1) {
          getGame.addPlayer(Player(1, name, secretWord))
          gameState = NextPlayer(0)
        }

      case _ =>
    }
    gameState

  }

  def nextPlayer: Int = {
    gameState = gameState match {
      case NextPlayer(0) => NextPlayer(1)
      case NextPlayer(1) => NextPlayer(0)
    }
    gameState.playerId
  }

  def guess(guesserId: Int, word: String, guesseeId: Int): Option[Answer] = game match {
    case None =>
      newGame
      None

    case Some(game) =>
      if (gameState == NextPlayer(guesserId)) {
        val numberOfMatchingLetters: Int = game.guess(word, guesseeId)

        if (numberOfMatchingLetters == 4)
          gameState = PlayerWon(guesserId)
        else
          nextPlayer

        Some(Answer(
          guesserId,
          numberOfMatchingLetters,
          gameState))
      } else None
  }

}
