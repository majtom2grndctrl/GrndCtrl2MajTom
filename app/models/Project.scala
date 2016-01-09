package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import play.api.libs.json._

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class Project (
  id: Option[Long],
  index: Int,
  title: String,
  filename: String,
  status: String,
  roles: String,
  tools: String,
  techStack: String,
  about: String
)

object Project {
  val simple = {
    get[Option[Long]]("project.id") ~
    get[Int]("project.index") ~
    get[String]("project.title") ~
    get[String]("project.filename") ~
    get[String]("project.status") ~
    get[String]("project.roles") ~
    get[String]("project.tools") ~
    get[String]("project.techStack") ~
    get[String]("project.about") map {
      case id ~ index ~ title ~ filename ~ status ~ roles ~ tools ~ techStack ~ about => Project(
        id, index, title, filename, status, roles, tools, techStack, about
      )
    }
  }

  def list():Seq[Project] = {
    DB.withConnection { implicit connection =>
      SQL("select * from project where `status`='public' order by `index` desc").as(Project.simple *)
    }
  }



}
