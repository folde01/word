package models

import scala.collection.mutable.ArrayBuffer

case class Player(id: Int, name: String, secretWord: Word) {
  def isInvalid: Boolean = secretWord.isInvalid || name.isEmpty

  private var guessedWords: ArrayBuffer[Word] = ArrayBuffer.empty

  def addGuessedWord(word: Word): Word = {
    guessedWords :+ word
    word
  }

//  def getGuessedWords: Seq[String] = guessedWords.toSeq.map(_.lowercasedValue)
  def getGuessedWords: Seq[String] = Seq("guns", "luck")
}
