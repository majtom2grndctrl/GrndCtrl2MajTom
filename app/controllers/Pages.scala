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

  def newPageForm() = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "title" -> nonEmptyText,
      "status" -> ignored("published"),
      "slug" -> nonEmptyText,
      "content" -> nonEmptyText,
      "description" -> optional(text), // optional, controller will need to handle for if no excerpt is specified
      "keywords" -> optional(text)
    )(Page.apply)(Page.unapply)
  )

  def display(path: String) = Action {
    Page.findBySlug(path).map { page =>
      Ok(html.page(page))
    }.getOrElse(NotFound)
  }

  def list() = AuthenticatedUser { user => implicit request =>
      Ok(html.manage.pages.list(Page.list()))
  }
  def create = AuthenticatedUser { user => implicit request =>
    Ok(
      html.manage.pages.newPage(
        newPageForm(), user
      )
    )
  }

  def edit(id: Long) = AuthenticatedUser { user => implicit request =>
    Page.findById(id).map { page =>
      Ok(
        html.manage.pages.editPage(
          newPageForm().fill(page), page
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
          html.manage.pages.editPage(
            newPageForm().fill(savedPage), savedPage
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
        Ok
      }
    )
  }
}