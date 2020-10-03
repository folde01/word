package models

sealed trait GameState {
  def playerId: Int
  def nextGameState: GameState = this match {
    case AddPlayer(0) => AddPlayer(1)
    case AddPlayer(1) => NextPlayer(0)
    case NextPlayer(0) => NextPlayer(1)
    case NextPlayer(1) => NextPlayer(0)
    case PlayerWon(_) => AddPlayer(0)
  }
}
final case class AddPlayer(playerId: Int) extends GameState
final case class NextPlayer(playerId: Int) extends GameState
final case class PlayerWon(playerId: Int) extends GameState

