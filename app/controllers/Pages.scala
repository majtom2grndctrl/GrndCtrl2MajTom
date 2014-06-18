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

object Pages extends Controller with Secured {

  def newPageForm() = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "title" -> nonEmptyText,
      "status" -> ignored("published"),
      "slug" -> nonEmptyText,
      "content" -> nonEmptyText,
      "description" -> optional(text), // optional, controller will need to handle for if no excerpt is specified
      "keywords" -> optional(text)
    )(Page.apply)(Page.unapply)
  )

  def display(path: String) = Action { implicit request =>
    Page.findBySlug(path).map { page =>
      Ok(html.pages.single(request.domain + request.uri, page))
    }.getOrElse(NotFound("Four-Oh-Four!"))
  }

  def list() = AuthenticatedUser { user => implicit request =>
    Ok(html.manage.pages.list(Page.list()))
  }

  def listJson() = AuthenticatedUser { user => implicit request =>
    Ok(Json.toJson(Page.list()))
  }
/*
  def singleJson(id: Long) = AuthenticatedUser { User => implicit request =>
    Ok(Json.toJson(Seq(Page.findById(id))))
  }
*/
  def create = AuthenticatedUser { user => implicit request =>
    Ok(
      html.manage.pages.newPage(
        user,
        newPageForm(),
        Page.list()
      )
    )
  }

  def edit(id: Long) = AuthenticatedUser { user => implicit request =>
    Page.findById(id).map { page =>
      Ok(
        html.manage.pages.editPage(
          user,
          newPageForm().fill(page),
          Page.list(),
          page
        )
      )
    }.getOrElse(NotFound)
  }

  def saveNew = AuthenticatedUser { user => implicit request =>
    newPageForm().bindFromRequest.fold(
      formWithErrors => BadRequest,
      page => {
        Page.create(page)
        val savedPage = Page.findNewestSaved(page.slug).get
        Ok(
          html.manage.pages.newPage(
            user,
            newPageForm().fill(savedPage),
            Page.list()
          )
        )
      }
    )
  }

  def saveUpdate(id: Long) = AuthenticatedUser { user => implicit request =>
    newPageForm().bindFromRequest.fold(
      errors => BadRequest,
      page => {
        Page.update(page, id)
        Ok(
          html.manage.pages.editPage(
            user,
            newPageForm().fill(page),
            Page.list(),
            Page.findById(id).get
          )
        )
      }
    )
  }

  def delete(id: Long) = AuthenticatedUser { user => implicit request =>
    Page.delete(id)
    Redirect(routes.Pages.create())
  }
}