package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date

import anorm._

import models._
import views._

object Application extends Controller with Secured {

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  val accountForm = Form(
    tuple(
      "newEmail" -> email,
	  "newFirstName" -> nonEmptyText,
	  "newLastName" -> nonEmptyText,
	  "newPassword" -> tuple(
	    "password1" -> text(minLength = 6),
	    "password2" -> text
	  ).verifying(
	    "Passwords don't match", passwords => passwords._1 == passwords._2
	  )
	)
  )

  def dateHelper(str: String): java.util.Date = new java.text.SimpleDateFormat("MM/dd/yyyy").parse(str)

  //Helpers for getting today's date as a string
  val dateToday = new Date()
  val dateStringHelper = new java.text.SimpleDateFormat("MM/dd/yyyy")
  val todayString: String = dateStringHelper.format(dateToday)

  def home = Action {
    Ok(html.index())
  }

  def login = Action { implicit request =>
    if(User.findAll.length == 0) {
      Results.Redirect(routes.Application.setup())
    } else {
      Ok(
        html.manage.login(
          loginForm,
          User.findAll.length
        )
      )
    }
  }

  def authenticate = Action { implicit request =>
    val users = User.findAll.length
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.manage.login(formWithErrors, users)),
      user => Redirect(routes.Application.dashboard).withSession("email" -> user._1)
    )
  }

  def setup = Action { implicit request =>
    Ok(
      html.manage.setup(accountForm)
    )
  }

  def saveFirstUser = Action { implicit request =>
    if(User.findAll.length == 0) {
      val post: BlogPost = BlogPost(
        NotAssigned, //id
        "Hello World", //title
        "published", //status
        Id(1), //author
        dateHelper("01/13/2013"), //published
        "hello-world", //slug
        """
<p>This is just a test entry. Please delete it by logging in to the backend.</p>
        """, //content
        Some("""
<p>This is just a test entry. Please delete it by logging in to the backend.</p>
        """)//excerpt
      )
      accountForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.manage.setup(formWithErrors)),
        {case(newEmail, newFirstName, newLastName, (password1, password2)) =>
          User.create(User(NotAssigned, newEmail, newFirstName, newLastName, password1))
          BlogPost.create(post)
          Redirect(routes.Application.dashboard).withSession("email" -> newEmail)
        }
      )
    } else {
      Redirect(routes.Application.login())
    }
  }

  def dashboard() = AuthenticatedUser { user => implicit request =>
    val dateHelper =  new java.text.SimpleDateFormat("mm/dd/yyyy")
    Ok(
      html.manage.dashboard(
        user,
        controllers.BlogPosts.newBlogPostForm(user)
      )
    )
  }

}

trait Secured {
  private def username(request: RequestHeader) = request.session.get("email")

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login())

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { userEmail: String =>
    Action(request => f(userEmail)(request))
  }

  def AuthenticatedUser(f: User => Request[AnyContent] => Result) = IsAuthenticated { username => implicit request =>
    User.findByEmail(username).map { user =>
      f(user)(request)
    }.getOrElse(onUnauthorized(request))
  }


}
