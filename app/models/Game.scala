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
    game = Some(Game())
    game
  }

  private def getGame: Game = game match {
    case None => newGame.get
    case Some(_) => game.get
  }

  def addPlayer(name: String, secretWord: String): Int = {
    gameState match {
      case AddPlayer(0) =>
        gameState = AddPlayer(1)
        getGame.addPlayer(Player(0, name, secretWord))

      case AddPlayer(1) =>
        gameState = NextPlayer(0)
        getGame.addPlayer(Player(1, name, secretWord))

    }
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
        nextPlayer
        Some(Answer(
          guesserId,
          game.guess(word, guesseeId),
          gameState))
      } else None
  }

}
