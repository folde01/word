package controllers

import javax.inject._
import models.{Answer, Game, GameState}
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
    val result: GameState = Game.addPlayer(playerId, name, secretWord)
    val redirectUrl: String =
      if (playerId == 0) "/addPlayerForm/1"
      else "/playerTurnForm/0"
    Redirect(redirectUrl)
  }

  def addPlayerForm(playerId: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val heading: String = s"Add player ${playerId}"
    val action: String = s"/addPlayer/${playerId}"
    Ok(views.html.addPlayer(playerId)(heading)(action))
  }

  def playerTurnForm(playerId: Int): Action[AnyContent] = Action {
    val heading: String = s"Player ${playerId}'s turn"
    val action: String = s"/player${playerId}turn"
    Ok(views.html.guess(playerId)(heading)(action))
  }

  def player0turn(word: String): Action[AnyContent] = Action {
    val guesserId: Int = 0
    val guesseeId: Int = 1
    val result: Option[Answer] = Game.guess(guesserId, word, guesseeId)
    Redirect("/playerTurnForm/1")
  }

  def player1turn(word: String): Action[AnyContent] = Action {
    val guesserId: Int = 1
    val guesseeId: Int = 0
    val result: Option[Answer] = Game.guess(guesserId, word, guesseeId)
    Redirect("/playerTurnForm/0")
  }

//  JSON API

  def newGame: Action[AnyContent] = Action {
    Game.newGame
    state
  }

  def guess(guesserId: Int, word: String, guesseeId: Int): Action[AnyContent] = Action {
    val result: Option[Answer] = Game.guess(guesserId, word, guesseeId)

    result match {
      case Some(answer) => Ok(Json.toJson(answer.toString))
      case None => state
    }
  }

  def addPlayerJson(playerId: Int, name: String, secretWord: String): Action[AnyContent] = Action {
    val result: GameState = Game.addPlayer(playerId, name, secretWord)
    Ok(Json.toJson(result.toString))
  }

  def gameState: Action[AnyContent] = Action {
    state
  }

  def state: Result = Ok(Json.toJson(Game.getGameState.toString))

}
