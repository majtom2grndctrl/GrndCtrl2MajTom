package controllers

import javax.inject.Inject

import play.api.mvc._

import views._
import models.{ ProjectService, SitePrefs }

@javax.inject.Singleton
class Projects @Inject() (projectService: ProjectService) extends Controller {
  def index() = Action { implicit request =>
    Ok(html.portfolio.index(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), projectService.list()))
  }
  def detail(slug: String) = Action { implicit request =>
    projectService.findBySlug(slug).map { case project =>
      Ok(html.portfolio.detail(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), project))
    }.getOrElse {
      NotFound(html.NotFound(request.domain + request.uri))
    }
  }
}
