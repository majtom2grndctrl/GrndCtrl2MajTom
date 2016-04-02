package models

import play.api.db.DB
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class User(id: Option[Long] = None, email: String, firstName: String, lastName: String, password: String )

object User {
  val simple = {
    get[Option[Long]]("user.id") ~
    get[String]("user.email") ~
    get[String]("user.firstName") ~
    get[String]("user.lastName") ~
    get[String]("user.password") map {
      case id~email~firstName~lastName~password => User(id, email, firstName, lastName, password)
    }
  }
}
