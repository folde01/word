package models

case class SecretWord(var secretWord: String = "duck") {
  def getNumber(guessedWord: String): Int = {
    secretWord.toSeq.intersect(guessedWord).unwrap.length
  }

  def setSecretWord(str: String): String = {
    secretWord = str
    secretWord
  }
}

object SecretWord {
  val secretWord = SecretWord()

  def getNumber(guessedWord: String) = secretWord.getNumber(guessedWord)

  def setSecretWord(str: String): String = secretWord.setSecretWord(str)

}
