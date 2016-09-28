package controllers

import javax.inject.Inject

import play.api.mvc._
import play.api.cache.Cached

import views._
import models.{ ProjectService, SitePrefs }

@javax.inject.Singleton
class Projects @Inject() (projectService: ProjectService, cached: Cached) extends Controller {
  def index() = cached("projectsHome") {
    Action {
      implicit request =>
      Ok(html.portfolio.index(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), projectService.list()))
    }
  }
  def detail(slug: String) = cached("project-" + slug) {
    Action {
      implicit request =>
      projectService.findBySlug(slug).map { case project =>
        Ok(html.portfolio.detail(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), project))
      }.getOrElse {
        NotFound(html.NotFound(request.domain + request.uri))
      }
    }
  }
}
