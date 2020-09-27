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
    val action: String = "addPlayer0"
    Ok(views.html.addPlayer(playerId)(heading)(action))
  }

  def addPlayer0(name: String, secretWord: String): Action[AnyContent] = Action {
    val result: GameState = Game.addPlayer(name, secretWord)
    Redirect("/addPlayer1Form")
  }

  def addPlayer1Form(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val playerId: Int = 1
    val heading: String = s"Add player ${playerId}"
    val action: String = "addPlayer1"
    Ok(views.html.addPlayer(playerId)(heading)(action))
  }

  def addPlayer1(name: String, secretWord: String): Action[AnyContent] = Action {
    val result: GameState = Game.addPlayer(name, secretWord)
    Redirect("/player0turnForm")
  }

  def player0turnForm(): Action[AnyContent] = Action {
    val playerId: Int = 0
    val heading: String = "Player 0's turn"
    val action: String = "player0turn"
    Ok(views.html.guess(playerId)(heading)(action))
  }

  def player0turn(word: String): Action[AnyContent] = Action {
    val guesserId: Int = 0
    val guesseeId: Int = 1
    val result: Option[Answer] = Game.guess(guesserId, word, guesseeId)
    Redirect("/player1turnForm")
  }

  def player1turnForm(): Action[AnyContent] = Action {
    val playerId: Int = 0
    val heading: String = "Player 1's turn"
    val action: String = "player1turn"
    Ok(views.html.guess(playerId)(heading)(action))
  }

  def player1turn(word: String): Action[AnyContent] = Action {
    val guesserId: Int = 1
    val guesseeId: Int = 0
    val result: Option[Answer] = Game.guess(guesserId, word, guesseeId)
    Redirect("/player0turnForm")
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

  def addPlayer(name: String, secretWord: String): Action[AnyContent] = Action {
    val result: GameState = Game.addPlayer(name, secretWord)
    Ok(Json.toJson(result.toString))
  }

  def gameState: Action[AnyContent] = Action {
    state
  }

  def state: Result = Ok(Json.toJson(Game.getGameState.toString))

}
