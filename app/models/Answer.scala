package models

case class Answer(guesserId: Int, word: Word, lettersInCommon: Int, gameState: GameState)
