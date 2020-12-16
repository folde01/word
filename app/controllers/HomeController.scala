package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import views.html.helper.form

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {

  import PlayerForm._
  //class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  val addPlayerPostUrl = routes.HomeController.addPlayerPost()

  def index = Action { implicit request: MessagesRequest[AnyContent] =>
    Game.newGame
    val playerId: Int = 0
    val heading: String = s"Welcome to Word - add player ${playerId}"
    //    val action: String = "/addPlayer/0"
    Ok(views.html.addPlayer(Some(playerId), heading, form,  addPlayerPostUrl))
    //    Ok(views.html.addPlayer(playerId, heading, action))
  }

//  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
//    Game.newGame
//    val playerId: Int = 0
//    val heading: String = s"Welcome to Word - add player ${playerId}"
//    val action: String = "/addPlayer/0"
//    Ok(views.html.addPlayer(playerId, heading, action))
//  }

  // This will be the action that handles our form post
  def createWidget = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[Data] =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.listWidgets(widgets.toSeq, formWithErrors, postUrl))
    }

    val successFunction = { data: Data =>
      // This is the good case, where the form was successfully parsed as a Data object.
      val widget = Widget(name = data.name, price = data.price)
      widgets += widget
      Redirect(routes.WidgetController.listWidgets()).flashing("info" -> "Widget added!")
    }

    val formValidationResult = form.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

  def addPlayerPost = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[Data] =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      val heading: String = "Player add failed"
      BadRequest(views.html.addPlayer(None, heading, formWithErrors, addPlayerPostUrl))
    }

    val successFunction = { data: Data =>

      val playerId: Int = data.playerId
      val name: String = data.name
      val secretWord: String = data.secretWord
      Game.addPlayer(playerId, name, Word(secretWord))
      redirectBasedOnGameState
    }


  }

  def redirectBasedOnGameState: Result = {

    val nextGameState: GameState = Game.getGameState

    val result: Result = nextGameState match {
      case AddPlayer(nextPlayerId) =>
        if (playerId == 0 && nextPlayerId == 1)
          addPlayerForm(nextPlayerId)
        else
          addPlayerForm(playerId)
      case NextPlayer(nextPlayerId) => playerTurnForm(nextPlayerId)
    }

    Redirect(
      result
    )

  }

  def addPlayer(playerId: Int, name: String, secretWord: String): Action[AnyContent] = Action {

    val nextGameState: GameState = Game.addPlayer(playerId, name, Word(secretWord))

    nextGameState match {
      case AddPlayer(nextPlayerId) =>
        if (playerId == 0 && nextPlayerId == 1)
          addPlayerForm(nextPlayerId)
        else
          addPlayerForm(playerId)
      case NextPlayer(nextPlayerId) => playerTurnForm(nextPlayerId)
    }
  }

  def addPlayerForm(playerId: Int, msg: String = ""): Result = {
    val formattedMsg: String = if (!msg.isEmpty) s" - ${msg}" else ""
    val heading: String = s"Add player ${playerId} ${formattedMsg}"
    val action: String = s"/addPlayer/${playerId}"
    Ok(views.html.addPlayer(playerId, heading, action))
  }

  def playerTurnForm(playerId: Int, msg: String = ""): Result = {
    val formattedMsg: String = if (!msg.isEmpty) s" - ${msg}" else ""
    val heading: String = s"Player ${playerId} - ${Game.playerName(playerId)}'s turn ${formattedMsg}"
    val action: String = s"/playerTurn/${playerId}"
    Ok(views.html.guess(playerId)(heading)(action)(Game.playerAnswers(playerId)))
  }

  def playerTurnFormRoute(playerId: Int, msg: String = ""): Action[AnyContent] = Action {
    playerTurnForm(playerId, msg)
  }

  def playerTurn(playerId: Int, word: String): Action[AnyContent] = Action {
    val guesserId: Int = playerId

    val guesseeId: Int =
      if (guesserId == 0) 1 else 0

    Game.guess(guesserId, Word(word), guesseeId) match {
      case None =>
        val msg: String = "Invalid guess - try again"
        playerTurnForm(playerId, msg)
      case Some(Answer(id, Word(word), lettersInCommon, state)) => {
        state match {
          case PlayerWon(playerId) => win(playerId)
          case _ =>
            answer(id, word, lettersInCommon)
        }
      }
    }

  }

  def answer(playerId: Int, word: String, inCommon: Int): Result = {
    val heading: String = s"${
      Game.playerName(playerId)
    } guessed ${
      word
    }. In common: ${
      inCommon
    }"
    val action: String = s"/playerTurnForm/${
      if (playerId == 0) 1 else 0
    }"
    Ok(views.html.answer(playerId)(heading)(action))
  }

  def win(playerId: Int): Result = {
    val heading: String = s"${Game.playerName(playerId)} wins!"
    val action: String = "/"
    Ok(views.html.win(heading)(action))
  }

  //  JSON API

  def newGame: Action[AnyContent] = Action {
    Game.newGame
    state
  }

  def guess(guesserId: Int, word: String, guesseeId: Int): Action[AnyContent] = Action {
    val result: Option[Answer] = Game.guess(guesserId, Word(word), guesseeId)

    result match {
      case Some(answer) => Ok(Json.toJson(answer.toString))
      case None => state
    }
  }

  def addPlayerJson(playerId: Int, name: String, secretWord: String): Action[AnyContent] = Action {
    val result: GameState = Game.addPlayer(playerId, name, Word(secretWord))
    Ok(Json.toJson(result.toString))
  }

  def gameState: Action[AnyContent] = Action {
    state
  }

  def state: Result = Ok(Json.toJson(Game.getGameState.toString))

}
