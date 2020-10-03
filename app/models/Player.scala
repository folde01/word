package models

case class Player(id: Int, name: String, secretWord: Word) {
  def isInvalid: Boolean = secretWord.isInvalid
}
