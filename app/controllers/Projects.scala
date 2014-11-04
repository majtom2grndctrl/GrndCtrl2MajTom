package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.functional.syntax._
import play.api.libs.json._

import anorm._

import views._
import models._

object Projects extends Controller {
  def index() = Action { implicit request =>
    Ok(html.portfolio.index(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), Project.list()))
  }
}