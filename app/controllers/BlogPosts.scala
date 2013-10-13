package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.util.{Date}

import anorm._

import views._
import models._

object BlogPosts extends Controller with Secured {

  def newBlogPostForm(user: User) = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "title" -> nonEmptyText,
      "status" -> ignored("published"),
      "author" -> ignored(user.id),
      "published" -> date("MM/dd/yyyy"),
      "slug" -> nonEmptyText,
      "content" -> nonEmptyText,
      "teaser" -> optional(text) // optional, controller will need to handle for if no excerpt is specified
    )(BlogPost.apply)(BlogPost.unapply)
  )

  val dateHelper =  new java.text.SimpleDateFormat("mm/dd/yyyy")

  //Helpers for getting today's date as a string
  val dateToday = new Date()
  val dateStringHelper = new java.text.SimpleDateFormat("MM/dd/yyyy")
  val todayString: String = dateStringHelper.format(dateToday)

  def index ( page: Int ) = Action {
    Option(BlogPost.findPageOfPosts(page).items).map { posts =>
      Ok(
        html.blog(posts)
      )
    }.getOrElse(NotFound)
  }

  def list(page: Int) = AuthenticatedUser { user => implicit request =>
    Option(BlogPost.findPageOfPosts(page).items).map { blogPosts =>
        Ok(
          html.blogPosts.list(
            blogPosts
          )
        )
    }.getOrElse(NotFound)
  }

  def create = AuthenticatedUser { user => implicit request =>
    Ok(
      html.manage.blogPosts.editor(
        newBlogPostForm(user), 0
      )
    )
  }

  def edit(id: Long) = AuthenticatedUser { user => implicit request =>
    BlogPost.findById(id).map { post =>
      Ok(
        html.manage.blogPosts.editor(
          newBlogPostForm(user).fill(post), id
        )
      )
    }.getOrElse(NotFound)
  }

  def saveNew = AuthenticatedUser { user => implicit request =>
    newBlogPostForm(user).bindFromRequest.fold(
      formWithErrors => BadRequest,
      post => {
        BlogPost.create(post)
        Ok(html.manage.dashboard(user, newBlogPostForm(user)))
      }
    )
  }

  def saveUpdate(id: Long) = AuthenticatedUser { user => implicit request =>
    newBlogPostForm(user).bindFromRequest.fold(
      errors => BadRequest,
      post => {
        BlogPost.update(post, id)
        Ok
      }
    )
  }
}