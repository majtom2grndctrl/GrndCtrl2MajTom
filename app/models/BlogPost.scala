package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import play.api.libs.json._

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

object BlogPost {
  val pegdown = new PegDownProcessor
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

  val withAuthor = BlogPost.processed ~ (User.simple) map {
    case post~user => (post, user)
  }

  val styles = List("blogpost", "micropost", "link")

  implicit val blogPostWrites = new Writes[Seq[(BlogPost, User)]] {
    def writes(posts: Seq[(BlogPost, User)]): JsValue = {
      Json.obj(
        "blogposts" -> posts.map { case (post, user) =>
          Json.obj(
            "post"-> Json.obj(
              "id" -> post.id.get,
              "title" -> post.title,
              "status" -> post.status,
              "style" -> post.style,
              "published" -> post.published,
              "slug" -> post.slug,
              "content" -> post.content,
              "description" -> post.description,
              "keywords" -> post.keywords
            ),
            "author" -> Json.obj(
              "first name" -> user.firstName,
              "last name" -> user.lastName
            )
          )
        }
      )
    }
  }

  implicit val singleWrites = new Writes[Option[(BlogPost, User)]] {
    def writes(p: Option[(BlogPost, User)]): JsValue = {
      Json.obj(
        "blogpost" -> p.map { case (post, user) =>
          Json.obj(
            "post" -> Json.obj(
              "id" -> post.id.get,
              "title" -> post.title,
              "status" -> post.status,
              "style" -> post.style,
              "published" -> post.published,
              "slug" -> post.slug,
              "content" -> post.content,
              "description" -> post.description,
              "keywords" -> post.keywords
            ),
            "author" -> Json.obj(
              "first name" -> user.firstName,
              "last name" -> user.lastName
            )
          )
        }
      )
    }
  }

// Save a new post or edited post
  def create(post: BlogPost) = {
 // Save a new blog post
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into blogpost values(
            null, {title}, {status}, {style}, {author}, {published}, {slug}, {content}, {description}, {keywords}
          )
        """
      ).on(
        'title -> post.title,
        'status -> post.status,
        'style -> post.style,
        'author -> post.author,
        'published -> post.published,
        'slug -> post.slug,
        'content -> post.content,
        'description -> post.description,
        'keywords -> post.keywords
      ).executeUpdate()
    }
  }

  def update(post: BlogPost, id: Long) = {
//Update an existing blog post
    
    DB.withConnection { implicit connection =>
      SQL(
        """
          update blogpost
          set title = {title}, status = {status}, style = {style}, author = {author}, published = {published}, slug = {slug}, content = {content}, description = {description}, keywords = {keywords}
          where blogpost.id = {id}
        """
      ).on(
        'id -> id,
        'title -> post.title,
        'status -> post.status,
        'style -> post.style,
        'author -> post.author,
        'published -> post.published,
        'slug -> post.slug,
        'content -> post.content,
        'description -> post.description,
        'keywords -> post.keywords
      ).executeUpdate()
    }
  }

// Retrieve a single post by ID
  def findById(postId: Long): Option[BlogPost] = {
    DB.withConnection { implicit connection =>
      SQL("select * from blogpost where blogpost.id = {postId}").on(
        'postId -> postId
      ).as(BlogPost.simple.singleOpt)
    }
  }

// Retrieve a single post by its slug
  def findBySlug(slug: String): Option[(BlogPost, User)] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          select * from blogpost
          left join user on blogpost.author = user.id
          where blogpost.slug = {slug}
        """
      ).on(
        'slug -> slug
      ).as(BlogPost.withAuthor.singleOpt)
    }
  }

// Retrieve a new post immediately after a user has saved it
  def findNewestSaved(authorId: Option[Long], slug: String): Option[BlogPost] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          select * from blogpost
          where blogpost.slug = {slug}
          limit 1
        """
      ).on(
        'slug -> slug
      ).as(BlogPost.simple.singleOpt)
    }
  }

  // Retrieve a BlogPostsPage of posts
  def findPageOfPosts(page: Int = 0, pageSize: Int = 10): BlogPostsPage[(BlogPost, User)] = {
    val offset = pageSize * page
    DB.withConnection { implicit connection =>
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
      ).as(BlogPost.withAuthor *)


      val totalRows = SQL(
        """
          select count(*) from blogpost
        """
      ).as(scalar[Long].single)

      BlogPostsPage(blogPosts, page, offset, totalRows)
    }
  }
  def delete(value: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from blogpost where id = {value}").on('value -> value).executeUpdate()
    }
  }
}

