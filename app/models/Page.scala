package models

import play.api.db.slick.Config.driver.simple._

case class Page (
  id: Option[Long],
  title: String,
  status: String,
  slug: String,
  content: String,
  description: Option[String] = None,
  keywords: Option[String] = None
)

class PagesTable(tag: Tag) extends Table[Page](tag, "page") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def status = column[String]("status", O.NotNull)
  def slug = column[String]("slug", O.NotNull)
  def content = column[String]("content", O.NotNull)
  def description = column[String]("description", O.Nullable)
  def keywords = column[String]("keywords", O.Nullable)

  def * = (id.?, title, status, slug, content, description.?, keywords.?) <>(Page.tupled, Page.unapply _)
}

object PagesTable {

  val page = TableQuery[PagesTable]

  def findBySlug(slug: String)(implicit s: Session): Option[Page] = {
    page.where(_.slug === slug).firstOption
  }

  def findById(id: Long)(implicit s: Session): Option[Page] = {
    page.where(_.id === id).firstOption
  }

  def list()(implicit s: Session): Seq[Page] = page.list;

  def saveNew(newPage: Page)(implicit s: Session) = {
    page.insert(newPage)
  }

  def saveEdits(editedPage: Page, id: Long)(implicit s: Session) = {
    page.where(_.id === id).update(editedPage)
  }

  def delete(id: Long)(implicit s: Session) = {
    page.where(_.id === id).delete
  }

}