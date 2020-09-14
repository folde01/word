package models

sealed trait GameState {
  def playerId: Int
}
final case class AddPlayer(playerId: Int) extends GameState
final case class NextPlayer(playerId: Int) extends GameState
final case class PlayerWon(playerId: Int) extends GameState
//final case object NewGame extends GameState

