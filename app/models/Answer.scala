package models

case class Answer(guesserId: Int, lettersInCommon: Int, gameState: GameState)
