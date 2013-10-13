package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.functional.syntax._

import anorm._

import views._
import models._

object Pages extends Controller with Secured {

  def display(path: String) = Action {
    Page.findBySlug(path).map { page =>
      Ok(html.page(page))
    }.getOrElse(NotFound)
    
  }

}