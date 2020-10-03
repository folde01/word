package models

import models.Word.WORD_LENGTH

case class Word(value: String) {

  def matches(word: Word): Option[Boolean] =
    if (word.isInvalid)
      None
    else
      Some(value.toLowerCase.equals(word.value.toLowerCase))

  def lettersInCommon(word: Word): Option[Int] = {
      if (word.isInvalid)
        None
      else Some(value
        .toSeq
        .intersect(word.value)
        .unwrap
        .length
    )
  }

  def isInvalid: Boolean = !isValid

  def isValid: Boolean =
    hasRightLength && hasRightNumberOfUniqueCharacters && hasLettersOnly

  private def hasRightLength = value.length.equals(Word.WORD_LENGTH)

  private def hasRightNumberOfUniqueCharacters: Boolean = value
    .toLowerCase
    .toSet[Char]
    .size
    .equals(WORD_LENGTH)

  private def hasLettersOnly: Boolean = value.forall(_.isLetter)

}

object Word {
  val WORD_LENGTH: Int = 4
}