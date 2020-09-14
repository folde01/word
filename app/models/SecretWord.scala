package models

case class SecretWord(secretWord: String) {
  def getNumber(guessedWord: String): Int =
    secretWord.toSeq.intersect(guessedWord).unwrap.length
}

object SecretWord {
  val secretWord = SecretWord("duck")

  def getNumber(guessedWord: String) = secretWord.getNumber(guessedWord)
}
