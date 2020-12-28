package controllers

import controllers.PlayerForm._
import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import play.api.i18n._
import play.api.data._




/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeControllerPost @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {

//class HomeControllerPost @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  // Web 1.0 client

  var game: Game = Game()

  def index(): Action[AnyContent] = Action { implicit request:  MessagesRequest[AnyContent] =>
    game = Game()
    val playerId: Int = 0
    val heading: String = s"Welcome to Word - add player ${playerId}"
//    val action: String = "/addPlayer/0"
//    Ok(views.html.addPlayer(playerId)(heading)(action))
    val postUrl = routes.HomeControllerPost.addPlayerPost()
    Ok(views.html.addPlayerPost(playerId, heading, form, postUrl))
//        Ok("yeah!!!!!!")
  }

  // This will be the action that handles our form post
  def addPlayerPost(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[Data] =>
//      BadRequest(views.html.listWidgets(widgets.toSeq, formWithErrors, postUrl))
      BadRequest("boooo!!!")
    }

    val successFunction = { data: Data =>
      Ok("yay!!!!")
    }

    val formValidationResult = form.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

  def addPlayer(playerId: Int, name: String, secretWord: String): Action[AnyContent] = Action {

    val nextGameState: GameState = game.addPlayer(playerId, name, Word(secretWord))

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
    Ok(views.html.addPlayer(playerId)(heading)(action))
  }

  def playerTurnForm(playerId: Int, msg: String = ""): Result = {
    val formattedMsg: String = if (!msg.isEmpty) s" - ${msg}" else ""
    val heading: String = s"Player ${playerId} - ${game.playerName(playerId)}'s turn ${formattedMsg}"
    val action: String = s"/playerTurn/${playerId}"
    Ok(views.html.guess(playerId)(heading)(action)(game.playerAnswers(playerId)))
  }

  def playerTurnFormRoute(playerId: Int, msg: String = ""): Action[AnyContent] = Action {
    playerTurnForm(playerId, msg)
  }

  def playerTurn(playerId: Int, word: String): Action[AnyContent] = Action {
    val guesserId: Int = playerId

    val guesseeId: Int =
      if (guesserId == 0) 1 else 0

    game.guess(guesserId, Word(word), guesseeId) match {
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
      game.playerName(playerId)
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
    val heading: String = s"${game.playerName(playerId)} wins!"
    val action: String = "/"
    Ok(views.html.win(heading)(action))
  }

  //  JSON API

  def newGame: Action[AnyContent] = Action {
    state
  }

  def guess(guesserId: Int, word: String, guesseeId: Int): Action[AnyContent] = Action {
    val result: Option[Answer] = game.guess(guesserId, Word(word), guesseeId)

    result match {
      case Some(answer) => Ok(Json.toJson(answer.toString))
      case None => state
    }
  }

  def addPlayerJson(playerId: Int, name: String, secretWord: String): Action[AnyContent] = Action {
    val result: GameState = game.addPlayer(playerId, name, Word(secretWord))
    Ok(Json.toJson(result.toString))
  }

  def gameState: Action[AnyContent] = Action {
    state
  }

  def state: Result = Ok(Json.toJson(game.getGameState.toString))

}
