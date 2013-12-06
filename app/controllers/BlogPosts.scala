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
      "status" -> ignored("public"),
      "style" -> ignored("blogpost"),
      "author" -> ignored(user.id),
      "published" -> date("MM/dd/yyyy"),
      "slug" -> nonEmptyText,
      "content" -> nonEmptyText,
      "description" -> optional(text),
      "keywords" -> optional(text)
    )(BlogPost.apply)(BlogPost.unapply)
  )

  val dateHelper =  new java.text.SimpleDateFormat("mm/dd/yyyy")

/*
  //Helpers for getting today's date as a string
  val dateToday = new Date()
  val dateStringHelper = new java.text.SimpleDateFormat("MM/dd/yyyy")
  val todayString: String = dateStringHelper.format(dateToday)
*/

  def index(page: Int) = Action { implicit request =>
    Option(BlogPost.findPageOfPosts(page).items).map { posts =>
      Ok(
        html.blogPosts.index(request.domain + request.uri, posts)
      )
    }.getOrElse(NotFound)
  }

  def single(slug: String) = Action { implicit request =>
    Option(BlogPost.findBySlug(slug).get).map { case(post, user) =>
      Ok(html.blogPosts.single(request.domain + request.uri, post, user))
    }.getOrElse(NotFound)
  }

  def list(page: Int) = AuthenticatedUser { user => implicit request =>
    Option(BlogPost.findPageOfPosts(page).items).map { blogPosts =>
        Ok(
          html.manage.blogPosts.list(
            blogPosts
          )
        )
    }.getOrElse(NotFound)
  }

  def listJson(page: Int) = AuthenticatedUser { user => implicit request =>
    Option(BlogPost.findPageOfPosts(page).items).map { blogPosts =>
      Ok(Json.toJson(blogPosts))
    }.getOrElse(NotFound)
  }
/* Deprecated async method
  def create = AuthenticatedUser { user => implicit request =>
    Ok(
      html.manage.blogPosts.newForm(
        newBlogPostForm(user)
      )
    )
  }
*/
  def edit(id: Long) = AuthenticatedUser { user => implicit request =>
    BlogPost.findById(id).map { post =>
      Ok(
        html.manage.blogPosts.editBlogPost(
          user,
          newBlogPostForm(user).fill(post),
          BlogPost.findPageOfPosts(0).items,
          post
        )
      )
    }.getOrElse(NotFound)
  }

  def saveNew = AuthenticatedUser { user => implicit request =>
    newBlogPostForm(user).bindFromRequest.fold(
      formWithErrors => BadRequest,
      post => {
        BlogPost.create(post)
        val savedPost = BlogPost.findNewestSaved(post.author, post.slug).get
        Ok(
          html.manage.dashboard(
            user,
            newBlogPostForm(user).fill(savedPost),
            BlogPost.findPageOfPosts(0).items
          )
        )
      }
    )
  }

  def saveUpdate(id: Long) = AuthenticatedUser { user => implicit request =>
    newBlogPostForm(user).bindFromRequest.fold(
      errors => BadRequest,
      post => {
        BlogPost.update(post, id)
        Ok(
          html.manage.blogPosts.editBlogPost(
            user,
            newBlogPostForm(user).fill(post),
            BlogPost.findPageOfPosts(0).items,
            BlogPost.findById(id).get
          )
        )
      }
    )
  }

  def delete(id: Long) = AuthenticatedUser { user => implicit request =>
    BlogPost.delete(id)
    Redirect(routes.Application.dashboard())
  }

}