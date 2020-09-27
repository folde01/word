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
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Game.newGame
    val playerId: Int = 0
    Ok(views.html.index(playerId))
  }

  def addPlayer1(player0name: String, player0secretWord: String): Action[AnyContent] = Action {
    val result: GameState = Game.addPlayer(player0name, player0secretWord)
    val playerId: Int = 1
    Ok(views.html.index(playerId))
  }


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
