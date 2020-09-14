package controllers

import javax.inject._
import models.Game
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
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def newGame = Action {
    Game.newGame
    Ok(Json.toJson("new game"))
  }

  def getNumber(guessedWord: String, secretHolderId: Int) = Action {
    val number: Int = Game.getNumber(guessedWord, secretHolderId)
    Ok(Json.toJson(number))
  }

  def addPlayer(id: Int, name: String, secretWord: String) = Action {
    val result: Int = Game.addPlayer(id, name, secretWord)
    Ok(Json.toJson(result))
  }
}
