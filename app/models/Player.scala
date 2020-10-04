package models

import scala.collection.mutable.ListBuffer

case class Player(id: Int, name: String, secretWord: GameWord) {
  def isInvalid: Boolean = secretWord.isInvalid || name.isEmpty

  private var guessedWords: ListBuffer[GameWord] = ListBuffer.empty

  def addGuessedWord(word: GameWord): GameWord = {
    guessedWords.append(word)
    word
  }

  def getGuessedWords: Seq[String] =
    guessedWords.toSeq.map(_.lowercasedValue)

}
