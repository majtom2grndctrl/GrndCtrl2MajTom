package controllers

import play.api.mvc._

import views._
import models.{ Project, SitePrefs }

class Projects extends Controller {
  def index() = Action { implicit request =>
    Ok(html.portfolio.index(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), Project.list()))
  }
  def detail(slug: String) = Action { implicit request =>
    Project.findBySlug(slug).map { case project =>
      Ok(html.portfolio.detail(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), project))
    }.getOrElse {
      NotFound(html.NotFound(request.domain + request.uri))
    }
  }
}
