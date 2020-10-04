package controllers

import javax.inject._
import models.{AddPlayer, Answer, Game, GameState, NextPlayer, PlayerWon, Word}
import play.api._
import play.api.libs.json.Json
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

    //    Game.getGameState match {
    //      case AddPlayer(playerId) => ???
    //      case _ => ???
    //    }

    val nextGameState: GameState = Game.addPlayer(playerId, name, Word(secretWord))

    val redirectUrl = nextGameState match {
      case AddPlayer(n) => {
        if (playerId == 0 && n == 1) s"/addPlayerForm/${n}"
        else {
          val msg: String = "Invalid player - try again"
          s"/addPlayerForm/${n}/${msg}"
        }
      }
      case NextPlayer(n) => s"/playerTurnForm/${n}"
    }
    Redirect(redirectUrl)

  }

  def addPlayerForm(playerId: Int, msg: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val formattedMsg: String = if (!msg.isEmpty) s" - ${msg}" else ""
    val heading: String = s"Add player ${playerId} ${formattedMsg}"
    val action: String = s"/addPlayer/${playerId}"
    Ok(views.html.addPlayer(playerId)(heading)(action))
  }

  def playerTurnForm(playerId: Int, msg: String): Action[AnyContent] = Action {
    val formattedMsg: String = if (!msg.isEmpty) s" - ${msg}" else ""
    val heading: String = s"Player ${playerId} - ${Game.playerName(playerId)}'s turn ${formattedMsg}"
    val action: String = s"/playerTurn/${playerId}"
    Ok(views.html.guess(playerId)(heading)(action))
  }

  def playerTurn(playerId: Int, word: String): Action[AnyContent] = Action {
    val guesserId: Int = playerId

    val guesseeId: Int =
      if (guesserId == 0) 1 else 0

    val result: Option[Answer] =
      Game.guess(guesserId, Word(word), guesseeId)

    val redirectUrl: String = result match {
      case None =>
        val msg: String = "Invalid guess - try again"
        s"/playerTurnForm/${playerId}/${msg}"
      case Some(Answer(id, Word(word), lettersInCommon, state)) => {
        state match {
          case PlayerWon(playerId) => s"/win/${id}"
          case _ => {
            val answers: String = Game.playerAnswers(playerId) match {
              case None => ""
              case Some(s) => s
            }
            s"/result/${id}/${word}/${lettersInCommon}/{$answers}"
          }
        }
      }
    }

    Redirect(redirectUrl)
  }


  def result(playerId: Int, word: String, inCommon: Int, answers: String): Action[AnyContent] = Action {
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
    Ok(views.html.result(playerId)(heading)(action)(answers))
  }

  def win(playerId: Int): Action[AnyContent] = Action {
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
