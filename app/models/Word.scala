package models

import models.Word.WORD_LENGTH

case class Word(value: String) {

  def lowercasedValue: String = value.toLowerCase

  def matches(word: Word): Option[Boolean] =
    if (word.isInvalid)
      None
    else
      Some(lowercasedValue.equals(word.lowercasedValue))

  def lettersInCommon(word: Word): Option[Int] = {
      if (word.isInvalid)
        None
      else Some(
        lowercasedValue
        .toSeq
        .intersect(word.lowercasedValue)
        .unwrap
        .length
    )
  }

  def isInvalid: Boolean = !isValid

  def isValid: Boolean =
    hasRightLength && hasRightNumberOfUniqueCharacters && hasLettersOnly

  private def hasRightLength = value.length.equals(Word.WORD_LENGTH)

  private def hasRightNumberOfUniqueCharacters: Boolean =
    lowercasedValue
    .toSet[Char]
    .size
    .equals(WORD_LENGTH)

  private def hasLettersOnly: Boolean = value.forall(_.isLetter)

}

object Word {
  val WORD_LENGTH: Int = 4
}