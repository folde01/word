package models

case class Word(value: String) {
  val isInvalid: Boolean = !value.length.equals(Word.WORD_LENGTH)
}

object Word {
  val WORD_LENGTH: Int = 4
}