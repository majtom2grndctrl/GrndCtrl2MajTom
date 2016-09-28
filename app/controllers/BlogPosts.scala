package controllers

import javax.inject.Inject

import play.api.mvc._
import play.api.cache.Cached

import java.util.{Date}

import views._
import models.{BlogPostService, SitePrefs}

class BlogPosts @Inject() (blogPostService: BlogPostService, cached: Cached) extends Controller {

  val dateHelper =  new java.text.SimpleDateFormat("mm/dd/yyyy")

  def index(page: Int) =
    cached("blogIndex") {
      Action { implicit request =>
      Some(blogPostService.findPageOfPosts(page).items).map { posts =>
        Ok(
          html.blogPosts.index(request.domain + request.uri, posts)
        )
      }.getOrElse(
        NotFound(html.NotFound(request.domain + request.uri))
      )
    }
  }

  def single(slug: String) =
    cached("blog-" + slug) {
      Action { implicit request =>
      blogPostService.findBySlug(slug).map { case(post, user) =>
        Ok(html.blogPosts.single(request.domain + request.uri, post, user))
      }.getOrElse(
        NotFound(html.NotFound(request.domain + request.uri))
      )
    }
  }

}
