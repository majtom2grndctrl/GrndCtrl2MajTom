package controllers

import javax.inject.Inject

import play.api.mvc._

import java.util.{Date}

import views._
import models.{BlogPostService, SitePrefs}

class BlogPosts @Inject() (blogPostService: BlogPostService) extends Controller {

  val dateHelper =  new java.text.SimpleDateFormat("mm/dd/yyyy")

  def index(page: Int) = Action { implicit request =>
    Some(blogPostService.findPageOfPosts(page).items).map { posts =>
      Ok(
        html.blogPosts.index(request.domain + request.uri, posts)
      )
    }.getOrElse(
      NotFound(html.NotFound(request.domain + request.uri))
    )
  }

  def single(slug: String) = Action { implicit request =>
    blogPostService.findBySlug(slug).map { case(post, user) =>
      Ok(html.blogPosts.single(request.domain + request.uri, post, user))
    }.getOrElse(
      NotFound(html.NotFound(request.domain + request.uri))
    )
  }

}
