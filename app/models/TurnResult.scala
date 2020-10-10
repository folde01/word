package models

case class TurnResult(symbol: String, price: Double)

object TurnResult {

//  import play.api.libs.json._
//
//  implicit object TurnResultFormat extends Format[TurnResult] {
//    // convert from JSON string to a TurnResult object (de-serializing from JSON)
//    override def reads(json: JsValue): JsResult[TurnResult] = {
//      val playerId: String = (json \ "playerId").as[String]
//      val word: Word = Word((json \ "word").as[String])
//      val inCommon: Int = (json \ "inCommon").as[Int]
//      val answers: Answers = (json \ "answers").as[Answers]
//      JsSuccess(Stock(symbol, price))
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

}


