package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._
import play.api.libs.json._

import java.util.{Date}

import views._
import models._

object BlogPosts extends Controller {

  val dateHelper =  new java.text.SimpleDateFormat("mm/dd/yyyy")

  def index(page: Int) = Action { implicit request =>
    Some(BlogPost.findPageOfPosts(page).items).map { posts =>
      Ok(
        html.blogPosts.index(request.domain + request.uri, posts)
      )
    }.getOrElse(
      NotFound(html.NotFound(request.domain + request.uri))
    )
  }

  def single(slug: String) = Action { implicit request =>
    BlogPost.findBySlug(slug).map { case(post, user) =>
      Ok(html.blogPosts.single(request.domain + request.uri, post, user))
    }.getOrElse(
      NotFound(html.NotFound(request.domain + request.uri))
    )
  }

}
