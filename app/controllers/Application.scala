package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._
//import java.util.Date

import anorm._

import models._
import views._

object Application extends Controller {

  def dateHelper(str: String): java.util.Date = new java.text.SimpleDateFormat("MM/dd/yyyy").parse(str)
/*
  def saveFirstUser = Action { implicit request =>
    if(User.findAll.length == 0) {
      val post: BlogPost = BlogPost(
        None, //id
        "Hello World", //title
        "published", //status
        "blogpost",//style
        Some(1), //author
        dateHelper("01/13/2013"), //published
        "hello-world", //slug
        """
<p>This is just a test entry. Please delete it by logging in to the backend.</p>
        """, //content
        Some("""
This is just a test entry. Please delete it by logging in to the backend.
        """),//excerpt
        Some("""Keywords""")//keywords
      )
      accountForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.manage.setup(formWithErrors)),
        {case(newEmail, newFirstName, newLastName, (password1, password2)) =>
          User.create(User(None, newEmail, newFirstName, newLastName, password1))
          BlogPost.create(post)
          Redirect(routes.Application.dashboard).withSession("email" -> newEmail)
        }
      )
    } else {
      Redirect(routes.Application.login())
    }
  }
*/
}
