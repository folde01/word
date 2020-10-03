package models

case class Player(id: Int, name: String, secretWord: String) {
  def isInvalid: Boolean = Word(secretWord).isInvalid
}
