package controllers

import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.db.slick._
//import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
//import play.api.mvc.BodyParsers._
import play.api.libs.json.Json
//import play.api.libs.json.Json._

import models._

object Pages extends Controller with Secured {

  val pages = TableQuery[PagesTable]

  def newPageForm() = Form(
    mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "status" -> ignored("published"),
      "slug" -> nonEmptyText,
      "content" -> nonEmptyText,
      "description" -> optional(text), // optional, controller will need to handle for if no excerpt is specified
      "keywords" -> optional(text)
    )(Page.apply)(Page.unapply)
  )

  implicit val pageFormat = Json.format[Page]

  def display(path: String) = DBAction { implicit request =>
    PagesTable.findBySlug(path).map { page =>
      Ok(views.html.pages.single(request.domain + request.uri, page))
    }.getOrElse(NotFound("Four-Oh-Four!"))
  }

  def list() = AuthenticatedUser { user => implicit request => 
    DB.withSession { implicit session =>
      Ok(views.html.manage.pages.list(PagesTable.list()))
    }
  }

  def listJson() = AuthenticatedUser { user => implicit request =>
    DB.withSession { implicit session => 
      Ok(Json.toJson(PagesTable.list()))
    }
  }

  def create = AuthenticatedUser { user => implicit request =>
    DB.withSession { implicit session =>
      Ok(
        views.html.manage.pages.newPage(
          User.findById(1).get,
          newPageForm(),
          PagesTable.list()
        )
      )
    }
  }

  def edit(id: Long) = AuthenticatedUser { user => implicit request =>
    DB.withSession { implicit session =>
      PagesTable.findById(id).map { page =>
        Ok(
          views.html.manage.pages.editPage(
            User.findById(1).get,
            newPageForm().fill(page),
            PagesTable.list(),
            page
          )
        )
      }.getOrElse(NotFound("Four-Oh-Four!"))
    }
  }

  def saveNew = AuthenticatedUser { user => implicit request =>
    DB.withSession { implicit session =>
      newPageForm().bindFromRequest.fold(
        formWithErrors => BadRequest,
        page => {
          PagesTable.saveNew(page)
          val savedPage = PagesTable.findBySlug(page.slug).get
          Ok(
            views.html.manage.pages.newPage(
              User.findById(1).get,
              newPageForm().fill(savedPage),
              PagesTable.list()
            )
          )
        }
      )
    }
  }

  def saveUpdate(id: Long) = AuthenticatedUser { user => implicit request =>
    DB.withSession { implicit session =>
      newPageForm().bindFromRequest.fold(
        errors => BadRequest,
        page => {
          PagesTable.saveEdits(page, id)
          Ok(
            views.html.manage.pages.editPage(
              User.findById(1).get,
              newPageForm().fill(page),
              PagesTable.list(),
              PagesTable.findById(id).get
            )
          )
        }
      )
    }
  }

  def delete(id: Long) = AuthenticatedUser { user => implicit request =>
    DB.withSession { implicit session =>
      PagesTable.delete(id)
      Redirect(routes.Pages.create())
    }
  }

}
