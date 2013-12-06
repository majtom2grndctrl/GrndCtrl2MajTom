package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import play.api.libs.json._

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class project (
  id: Pk[Long],
  title: String,
  status: String,
  style: String,
  author: Pk[Long],
  published: Date,
  slug: String,
  content: String,
  description: Option[String],
  keywords: Option[String],
  url: Option[String],
  roles: String
)