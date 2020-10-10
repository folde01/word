package controllers

import javax.inject._
import models.{AddPlayer, Answer, Game, GameState, NextPlayer, PlayerWon, Stock, TurnResult, Word}
import play.api._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  // Web 1.0 client

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Game.newGame
    val playerId: Int = 0
    val heading: String = s"Welcome to Word - add player ${playerId}"
    val action: String = "/addPlayer/0"
    Ok(views.html.addPlayer(playerId)(heading)(action))
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
    Ok(views.html.addPlayer(playerId)(heading)(action))
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

  def saveStock = Action { request =>
    val json = request.body.asJson.get
    val stock = json.as[Stock]
    println(stock)
    Ok
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

  def getStock = Action {
    val stock = Stock("GOOG", 650.0)
    Ok(Json.toJson(stock))
  }

}
