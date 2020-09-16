package controllers

import javax.inject._
import models.{Answer, Game}
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
    Ok(views.html.index())
  }

  def newGame: Action[AnyContent] = Action {
    Game.newGame
    Ok(Json.toJson(Game.getGameState.toString))
  }

  def guess(guesserId: Int, word: String, guesseeId: Int): Action[AnyContent] = Action {
    val result: Option[Answer] = Game.guess(guesserId, word, guesseeId)

    Ok(Json.toJson(result match {
      case Some(answer) => answer.toString
      case None => "Wrong game state"
    }))
  }

  def addPlayer(name: String, secretWord: String): Action[AnyContent] = Action {
    val result: Int = Game.addPlayer(name, secretWord)
    Ok(Json.toJson(Game.getGameState.toString))
  }

  def gameState: Action[AnyContent] = Action {
    Ok(Json.toJson(Game.getGameState.toString))
  }

}
