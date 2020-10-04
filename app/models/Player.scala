package models

import scala.collection.mutable.ListBuffer

case class Player(id: Int, name: String, secretWord: Word) {
  def isInvalid: Boolean = secretWord.isInvalid || name.isEmpty

  private var guessedWords: ListBuffer[Word] = ListBuffer.empty
  private var answers: ListBuffer[Answer] = ListBuffer.empty

  def addGuessedWord(word: Word): Word = {
    guessedWords.append(word)
    word
  }

  def addAnswer(answer: Answer): Answer = {
    answers.append(answer)
    answer
  }

  def getAnswers: Seq[Answer] = answers.toSeq

  def getGuessedWords: Seq[String] =
    guessedWords.toSeq.map(_.lowercasedValue)

}
