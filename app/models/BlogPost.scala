package models

import javax.inject.Inject

import java.util.{Date}

import play.api.db.Database
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import org.pegdown.PegDownProcessor

import scala.language.postfixOps

case class BlogPost (
  id: Option[Long] = None,
  title: String,
  status: String,
  style: String,
  author: Option[Long],
  published: Date,
  slug: String,
  content: String,
  description: Option[String],
  keywords: Option[String]
)

case class BlogPostsPage[A](items: Seq[A], BlogPostsPage: Int, offset: Long, total: Long) {
  lazy val prev = Option(BlogPostsPage -1).filter(_ >= 0 )
  lazy val next = Option(BlogPostsPage +1).filter(_ => (offset + items.size) < total)
}

class BlogPostService @Inject() (db: Database) {
  val pegdown = new PegDownProcessor(3)
  val simple = {
    get[Option[Long]]("blogPost.id") ~
    get[String]("blogPost.title") ~
    get[String]("blogPost.status") ~
    get[String]("blogPost.style") ~
    get[Option[Long]]("blogPost.author") ~
    get[Date]("blogPost.published") ~
    get[String]("blogPost.slug") ~
    get[String]("blogPost.content") ~
    get[Option[String]]("blogPost.description") ~
    get[Option[String]]("blogPost.keywords") map {
      case id~title~status~style~author~published~slug~content~description~keywords => BlogPost(
        id, title, status, style, author, published, slug, content, description, keywords
      )
    }
  }

  val processed = {
    get[Option[Long]]("blogPost.id") ~
    get[String]("blogPost.title") ~
    get[String]("blogPost.status") ~
    get[String]("blogPost.style") ~
    get[Option[Long]]("blogPost.author") ~
    get[Date]("blogPost.published") ~
    get[String]("blogPost.slug") ~
    get[String]("blogPost.content") ~
    get[Option[String]]("blogPost.description") ~
    get[Option[String]]("blogPost.keywords") map {
      case id~title~status~style~author~published~slug~content~description~keywords => BlogPost(
        id, title, status, style, author, published, slug, pegdown.markdownToHtml(content), description, keywords
      )
    }
  }

  val withAuthor = processed ~ (User.simple) map {
    case post~user => (post, user)
  }

  val styles = List("blogpost", "micropost", "link")

// Retrieve a single post by its slug
  def findBySlug(slug: String): Option[(BlogPost, User)] = {
    db.withConnection { implicit connection =>
      SQL(
        """
          select * from blogpost
          left join user on blogpost.author = user.id
          where blogpost.slug = {slug}
        """
      ).on(
        'slug -> slug
      ).as(withAuthor.singleOpt)
    }
  }

  // Retrieve a BlogPostsPage of posts
  def findPageOfPosts(page: Int = 0, pageSize: Int = 10): BlogPostsPage[(BlogPost, User)] = {
    val offset = pageSize * page
    db.withConnection { implicit connection =>
      val blogPosts = SQL(
        """
          select * from blogpost
          left join user on blogpost.author = user.id
          order by published desc
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize,
        'offset -> offset
      ).as(withAuthor *)


      val totalRows = SQL(
        """
          select count(*) from blogpost
        """
      ).as(scalar[Long].single)

      BlogPostsPage(blogPosts, page, offset, totalRows)
    }
  }
}
