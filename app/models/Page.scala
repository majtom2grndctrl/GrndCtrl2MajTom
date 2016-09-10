package models

import javax.inject.Inject

import java.util.{Date}

import play.api.db.Database
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

@javax.inject.Singleton
class PageService @Inject() (db: Database) {
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
  def list(): Seq[Page] = db.withConnection { implicit connection =>
    SQL("select * from page").as(simple *)
  }

// Retrieve a single page by its slug
  def findBySlug(slug: String): Option[Page] = db.withConnection { implicit connection =>
    SQL("select * from page where page.slug = {slug}").on(
      'slug -> slug
    ).as(simple.singleOpt)
  }
}
