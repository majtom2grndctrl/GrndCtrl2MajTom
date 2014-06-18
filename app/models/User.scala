package models

import play.api.db._
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

  def findById(userId: Long):Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where id = {userId}").on(
        'userId -> userId
      ).as(User.simple.singleOpt)
    }
  }

  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email
      ).as(User.simple.singleOpt)
    }
  }

  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.simple *)
    }
  }
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where 
         email = {email} and password = {password}
        """
      ).on(
        'email -> email,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }

  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user values (
            null, {email}, {firstName}, {lastName}, {password}
          )
        """
      ).on(
        'email -> user.email,
        'firstName -> user.firstName,
        'lastName -> user.lastName,
        'password -> user.password
      ).executeUpdate()      
      user
    }
  }

  def update(id: Long, user: User) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update user set email = {email}, firstName = {firstName}, lastName = {lastName}, password = {password}
          where id = {id}
        """
      ).on(
        'id -> id,
        'email -> user.email,
        'firstName -> user.firstName,
        'lastName -> user.lastName,
        'password -> user.password
      ).executeUpdate()
    }
  }
}