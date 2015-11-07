package controllers

import play.api.mvc._
import play.api.libs.functional.syntax._

import views._
import models._

object Pages extends Controller {

  def index() = Action { implicit request =>
    Ok(html.index(request.domain + request.uri, SitePrefs.name, Some(null), Some(null)))
  }

  def display(path: String) = Action { implicit request =>
    Page.findBySlug(path).map { page =>
      Ok(html.pages.single(request.domain + request.uri, page))
    }.getOrElse(NotFound("Four-Oh-Four!"))
  }

}
