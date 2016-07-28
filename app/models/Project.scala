package models

import java.util.{Date}

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._

import anorm._
import anorm.SqlParser._

import org.pegdown.PegDownProcessor

case class Project (
  id: Option[Long],
  index: Int,
  title: String,
  filename: String,
  status: String,
  roles: String,
  tools: String,
  techStack: String,
  about: String,
  slug: Option[String],
  indepth: Option[String]
)

object Project {
  val pegdown = new PegDownProcessor
  val simple = {
    get[Option[Long]]("project.id") ~
    get[Int]("project.index") ~
    get[String]("project.title") ~
    get[String]("project.filename") ~
    get[String]("project.status") ~
    get[String]("project.roles") ~
    get[String]("project.tools") ~
    get[String]("project.techStack") ~
    get[String]("project.about") ~
    get[Option[String]]("project.slug") ~
    get[Option[String]]("project.indepth") map {
      case id ~ index ~ title ~ filename ~ status ~ roles ~ tools ~ techStack ~ about ~ slug ~ indepth => Project(
        id, index, title, filename, status, roles, tools, techStack, pegdown.markdownToHtml(about), slug, Some(pegdown.markdownToHtml(indepth.getOrElse("")))
      )
    }
  }

  def list():Seq[Project] = {
    DB.withConnection { implicit connection =>
      SQL("select * from project where `status`='public' order by `index` desc").as(Project.simple *)
    }
  }

  def findBySlug(slug: String): Option[Project] = {
    DB.withConnection { implicit connection =>
      SQL("select * from project where project.slug = {slug}")
        .on( 'slug -> slug )
        .as(Project.simple.singleOpt)
    }
  }

}
