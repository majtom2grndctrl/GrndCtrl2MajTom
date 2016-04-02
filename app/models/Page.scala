package models

import java.util.{Date}

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps


case class Page (
  id: Option[Long],
  title: String,
  status: String,
  slug: String,
  content: String,
  description: Option[String],
  keywords: Option[String]
)

object Page {
  val simple = {
    get[Option[Long]]("page.id") ~
    get[String]("page.title") ~
    get[String]("page.status") ~
    get[String]("page.slug") ~
    get[String]("page.content") ~
    get[Option[String]]("page.description") ~
    get[Option[String]]("page.keywords") map {
      case id~title~status~slug~content~description~keywords => Page(
        id, title, status, slug, content, description, keywords
      )
    }
  }

  //List pages
  def list(): Seq[Page] = DB.withConnection { implicit connection =>
    SQL("select * from page").as(Page.simple *)
  }

// Retrieve a single page by its slug
  def findBySlug(slug: String): Option[Page] = DB.withConnection { implicit connection =>
    SQL("select * from page where page.slug = {slug}").on(
      'slug -> slug
    ).as(Page.simple.singleOpt)
  }
}
