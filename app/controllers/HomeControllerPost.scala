package controllers

import controllers.GuessForm._

import javax.inject._
import models._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import play.api.i18n._
import play.api.data._
import play.api.http.Writeable.wByteArray


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

  def log(str: String): Unit = println(s"- - - ${str} - - -")

  var game: Game = Game()

  val addPlayerPostUrl = routes.HomeControllerPost.handleAddPlayerPost()
  val squarePostUrl = routes.HomeControllerPost.squarePost()
  val guessHandlerUrl = routes.HomeControllerPost.handleGuessPost()
  val playerTurnFormUrl = routes.HomeControllerPost.playerTurnFormAction()

  //  val playerOneId: Int = 1000
  //  val playerTwoId: Int = 1001

  def squareIndex(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    log("squareIndex")
    Ok(views.html.squarePost(squarePostUrl))
  }

  // This will be the action that handles our form post

  def squarePost = Action { implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals.map { args =>
      val n = args("n").head
      Ok((n.toInt * n.toInt).toString)
    }.getOrElse(Ok("BAD THING HAPPEN"))
  }

  def indexAjax(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    game = Game()
    val playerId: Int = 0

    Ok(views.html.spa(views.html.spaWelcome()))
      .withSession("playerId" -> playerId.toString)
  }

  def apiAddPlayer(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val postVals = request.body.asFormUrlEncoded
    val playerId: Int = request.session.get("playerId").getOrElse("NO_ID").toInt

    postVals.map { args =>
      val playerName = args("playerName").head
      val secretWord = args("secretWord").head

      val nextGameState: GameState = game.addPlayer(playerId, playerName, Word(secretWord))

      val result: Result = nextGameState match {
        case AddPlayer(nextPlayerId) =>
          if (playerId == 0 && nextPlayerId == 1) {
            //          log(s"added player ${playerId}, now add player ${nextPlayerId}")
            addPlayerForm(nextPlayerId)
          } else {
            //          log(s"now add player ${playerId}")
            addPlayerForm(playerId)
          }
        case NextPlayer(nextPlayerId) => {
          playerTurnForm(nextPlayerId)
        }
      }
      Ok(nextGameState.toString)
    }.getOrElse(Ok("BAD THING HAPPEN"))
  }

  def index(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    import controllers.PlayerForm._
    game = Game()
    val playerId: Int = 0
    val heading: String = s"Welcome to Word - add player ${playerId}"
    Ok(views.html.addPlayerPost(heading, form, addPlayerPostUrl))
      .withSession("playerId" -> playerId.toString)
  }

  // This will be the action that handles our form post
  def handleAddPlayerPost(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    import controllers.PlayerForm._
    val errorFunction: Form[PlayerData] => Result = {
      formWithErrors: Form[PlayerData] =>
        BadRequest(views.html.addPlayerPost("Uh oh, bad player add!", formWithErrors, addPlayerPostUrl))
    }

    val playerId: String = request.session.get("playerId").getOrElse("NO_ID")
    val successFunction: PlayerData => Result = {
      data: PlayerData => addPlayer(playerId.toInt, data.name, data.secretWord)
    }

    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }

  def addPlayer(playerId: Int, name: String, secretWord: String)(implicit request: MessagesRequest[AnyContent]): Result = {

    val nextGameState: GameState = game.addPlayer(playerId, name, Word(secretWord))

    nextGameState match {
      case AddPlayer(nextPlayerId) =>
        if (playerId == 0 && nextPlayerId == 1) {
          //          log(s"added player ${playerId}, now add player ${nextPlayerId}")
          addPlayerForm(nextPlayerId)
        } else {
          //          log(s"now add player ${playerId}")
          addPlayerForm(playerId)
        }
      case NextPlayer(nextPlayerId) => {
        playerTurnForm(nextPlayerId)
      }
    }
  }

  /*
   / -> index() --id=0--> views.html.addPlayerPost -> addPlayerPost() -> addPlayer(0) ->
  game.addPlayer(0) -> addPlayerForm(1) -> views.html.addPlayerPost(1) ->
   */

  def addPlayerForm(playerId: Int, msg: String = "")(implicit request: MessagesRequest[AnyContent]): Result = {
    import controllers.PlayerForm._
    val formattedMsg: String = if (!msg.isEmpty) s" - ${msg}" else ""
    val heading: String = s"Add player ${playerId} ${formattedMsg}"
    Ok(views.html.addPlayerPost(heading, form, addPlayerPostUrl)).withSession("playerId" -> playerId.toString)
  }


  def playerTurnForm(playerId: Int, msg: String = "")(implicit request: MessagesRequest[AnyContent]): Result = {
    import controllers.GuessForm._
    val formattedMsg: String = if (!msg.isEmpty) s" - ${msg}" else ""
    val heading: String = s"Player ${playerId} - ${game.playerName(playerId)}'s turn ${formattedMsg}"
    Ok(views.html.guessPost(heading, game.playerAnswers(playerId), form, guessHandlerUrl)).withSession("playerId" -> playerId.toString)
  }

  def playerTurnFormAction(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    val playerId: Int = request.session.get("playerId").getOrElse("NO_ID").toInt
    log(s"id: ${playerId}, answers: ${game.playerAnswers(playerId)}")
    playerTurnForm(playerId)
  }

  // This will be the action that handles our form post
  def handleGuessPost(): Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    import controllers.GuessForm._
    val errorFunction: Form[GuessData] => Result = {
      formWithErrors: Form[GuessData] =>
        BadRequest(views.html.guessPost("Uh oh, bad guess!", Seq.empty, formWithErrors, guessHandlerUrl))
    }

    val playerId: String = request.session.get("playerId").getOrElse("NO_ID")
    val successFunction: GuessData => Result = {
      data: GuessData => playerTurn(playerId.toInt, data.guess)
    }

    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }

  //  def playerTurnFormRoute(playerId: Int, msg: String = ""): Action[AnyContent] = Action {
  //    playerTurnForm(playerId, msg)
  //  }

  def playerTurn(playerId: Int, word: String)(implicit request: MessagesRequest[AnyContent]): Result = {
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

  def answer(playerId: Int, word: String, inCommon: Int)(implicit request: MessagesRequest[AnyContent]): Result = {
    import controllers.NextPlayerForm._ // can we use an empty form here if this is being passed in session?
    val name: String = game.playerName(playerId)
    log(s"name: ${name}")
    val heading: String = s"${name} guessed ${word}. In common: ${inCommon}"
    //    val action: String = s"/playerTurnForm/${
    //      if (playerId == 0) 1 else 0
    //    }"
    val nextPlayerId: Int = if (playerId == 0) 1 else 0
    Ok(views.html.answerAndNextPlayerPost(playerId, heading, form, playerTurnFormUrl)).withSession("playerId" -> nextPlayerId.toString)
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
