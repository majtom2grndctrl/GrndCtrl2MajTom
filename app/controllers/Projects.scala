package controllers

import javax.inject.Inject
import scala.concurrent.duration._

import play.api.mvc._
import play.api.cache._

import views._
import models.{ Project, ProjectService, SitePrefs }

@javax.inject.Singleton
class Projects @Inject() (projectService: ProjectService, cache: CacheApi) extends Controller {
  def index() = Action {
    implicit request =>
    val projectList = cache.getOrElse[Seq[Project]]("projects.list", 30.minutes) {
      projectService.list()
    }
    Ok(html.portfolio.index(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), projectList))
  }

  def detail(slug: String) = Action {
    implicit request =>
    val projectCache = cache.getOrElse[Option[Project]]("project.detail." + slug, 30.minutes) {
      projectService.findBySlug(slug)
    }
    projectCache.map { case project =>
      Ok(html.portfolio.detail(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), project))
    }.getOrElse {
      NotFound(html.NotFound(request.domain + request.uri))
    }
  }
}
