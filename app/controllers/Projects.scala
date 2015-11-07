package controllers

import play.api.mvc._

import views._
import models._

object Projects extends Controller {
  def index() = Action { implicit request =>
    Ok(html.portfolio.index(request.domain + request.uri, "Portfolio - " + SitePrefs.name, Some("The portfolio of Dan Hiester"), Some("portfolio, design, UX design"), Project.list()))
  }
}
