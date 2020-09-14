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

//  var game: Option[Game] = None
  var game: Game = null

  def newGame: Game = {
    game = Game()
    game
  }

  def addPlayer(id: Int, name: String, secretWord: String): Int =
    game.addPlayer(Player(id, name, secretWord))

  def getNumber(guessedWord: String, secretHolderId: Int) =
    game.getNumber(guessedWord, secretHolderId)

}
