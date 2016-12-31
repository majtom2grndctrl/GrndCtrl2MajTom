package controllers

import javax.inject.Inject
import scala.concurrent.duration._

import play.api.mvc._
import play.api.cache.CacheApi

import java.util.{Date}

import views._
import models.{BlogPost, BlogPostService, SitePrefs}

class BlogPosts @Inject() (blogPostService: BlogPostService, cache: CacheApi) extends Controller {

  val dateHelper =  new java.text.SimpleDateFormat("mm/dd/yyyy")

  def index(page: Int) = Action { implicit request =>
    Some(blogPostService.findPageOfPosts(page).items).map { posts =>
      val postsList = cache.getOrElse[Seq[(BlogPost, models.User)]]("blogpost.list") {
        posts
      }
      Ok(
        html.blogPosts.index(request.domain + request.uri, postsList)
      )
    }.getOrElse(
      NotFound(html.NotFound(request.domain + request.uri))
    )
  }

  def single(slug: String) =
    Action { implicit request =>
    val blogCache = cache.getOrElse[Option[(BlogPost, models.User)]]("blogpost." + slug) {
      blogPostService.findBySlug(slug)
    }
    blogCache.map { case(post, user) =>
      Ok(html.blogPosts.single(request.domain + request.uri, post, user))
    }.getOrElse(
      NotFound(html.NotFound(request.domain + request.uri))
    )
  }

}
