package models

case class Answers(answers: Seq[Answer])

//object Answers {
//
//  import play.api.libs.json._
//
//  implicit object AnswersFormat extends Format[Answers] {
//    // convert from JSON string to a Stock object (de-serializing from JSON)
//    override def reads(json: JsValue): JsResult[Answers] = {
//      val answers: Seq[Answers] = (json \\ "answer").as[Answer]
//      JsSuccess(Answers(answers))
//    }
//
//    // convert from Stock object to JSON (serializing to JSON)
//    override def writes(s: Stock): JsValue = {
//      // JsObject requires Seq[(String, play.api.libs.json.JsValue)]
//      val stockAsList = Seq("symbol" -> JsString(s.symbol),
//        "price" -> JsNumber(s.price))
//      JsObject(stockAsList)
//    }
//  }
//
//}
