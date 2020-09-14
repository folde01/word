package models

import scala.collection.mutable.ListBuffer

case class Game() {

  def getNumber(guessedWord: String, secretHolderId: Int): Int = {
    players(secretHolderId).secretWord.toSeq.intersect(guessedWord).unwrap.length
  }

  private var players: ListBuffer[Player] = ListBuffer.empty

  def addPlayer(player: Player): Int = {
    players += player
    player.id
  }

}

object Game {

  private var game: Option[Game] = None

  def newGame: Option[Game] = {
    game = Some(Game())
    game
  }

  private def getGame: Game = game match {
    case None => newGame.get
    case Some(_) => game.get
  }

  def addPlayer(id: Int, name: String, secretWord: String): Int =
    getGame.addPlayer(Player(id, name, secretWord))

  def getNumber(guessedWord: String, secretHolderId: Int) =
    getGame.getNumber(guessedWord, secretHolderId)

}
