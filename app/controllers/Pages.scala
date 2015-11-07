package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.functional.syntax._
import play.api.libs.json._

import anorm._

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
