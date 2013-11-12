package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import play.api.libs.json._

import anorm._
import anorm.SqlParser._

case class Page (
  id: Pk[Long],
  title: String,
  status: String,
  slug: String,
  content: String,
  description: Option[String],
  keywords: Option[String]
)

object Page {
  val simple = {
    get[Pk[Long]]("page.id") ~
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
/*
  implicit val pageWrites = new Writes[Page] {
    def writes(page: Page) = {
      Json.obj(
        "page" -> Json.obj(
            "id" -> page.id.get,
            "title" -> page.title,
            "status" -> page.status,
            "slug" -> page.slug,
            "content" -> page.content,
            "description" -> page.description,
            "keywords" -> page.keywords
          )
      )
    }
  }
*/
  
  implicit val pagesWrites = new Writes[Seq[Page]] {
    def writes(pages: Seq[Page]) = {
      Json.obj(
        "pages" -> pages.map { page =>
          Json.obj(
            "id" -> page.id.get,
            "title" -> page.title,
            "status" -> page.status,
            "slug" -> page.slug,
            "content" -> page.content,
            "description" -> page.description,
            "keywords" -> page.keywords
          )
        }
      )
    }
  }

//  implicit val pageReads = Json.reads[Page]

  //List pages
  def list(): Seq[Page] = {
    DB.withConnection { implicit connection =>
      SQL("select * from page").as(Page.simple *)
    }
  }

// Save a new page or edited page
  def create(page: Page) = {
 // Save a new page
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into page values(
            null, {title}, {status}, {slug}, {content}, {description}, {keywords}
          )
        """
      ).on(
        'title -> page.title,
        'status -> page.status,
        'slug -> page.slug,
        'content -> page.content,
        'description -> page.description,
        'keywords -> page.keywords
      ).executeUpdate()
    }
 }

  def update(page: Page, id: Long) = {
//Update an existing page
    
    DB.withConnection { implicit connection =>
      SQL(
        """
          update page
          set title = {title}, status = {status}, slug = {slug}, content = {content}, description = {description}, keywords = {keywords}
          where page.id = {id}
        """
      ).on(
        'id -> id,
        'title -> page.title,
        'status -> page.status,
        'slug -> page.slug,
        'content -> page.content,
        'description -> page.description,
        'keywords -> page.keywords
      ).executeUpdate()
    }
  }
  
// Retrieve a single page by its slug
  def findBySlug(slug: String): Option[Page] = {
    DB.withConnection { implicit connection =>
      SQL("select * from page where page.slug = {slug}").on(
        'slug -> slug
      ).as(Page.simple.singleOpt)
    }
  }

// Retrieve a single page by its DB ID
  def findById(id: Long): Option[Page] = {
    DB.withConnection { implicit connection =>
      SQL("select * from page where page.id = {id}").on(
        'id -> id
      ).as(Page.simple.singleOpt)
    }
  }

  def findNewestSaved(slug: String): Option[Page] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          select * from page
          where page.slug = {slug}
          limit 1
        """
      ).on(
        'slug -> slug
      ).as(Page.simple.singleOpt)
    }
  }
}
