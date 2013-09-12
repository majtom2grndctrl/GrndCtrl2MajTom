package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current
import play.api.libs.json._

import anorm._
import anorm.SqlParser._

case class BlogPost (
  id: Pk[Long],
  title: String,
  status: String,
  author: Pk[Long],
  published: Date,
  slug: Option[String],
  content: String,
  excerpt: Option[String]
)

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page -1).filter(_ >= 0 )
  lazy val next = Option(page +1).filter(_ => (offset + items.size) < total)
}

object BlogPost {
  var simple = {
    get[Pk[Long]]("blogPost.id") ~
    get[String]("blogPost.title") ~
    get[String]("blogPost.status") ~
    get[Pk[Long]]("blogPost.author") ~
    get[Date]("blogPost.published") ~
    get[Option[String]]("blogPost.slug") ~
    get[String]("blogPost.content") ~
    get[Option[String]]("blogPost.excerpt") map {
      case id~title~status~author~published~slug~content~teaser => BlogPost(
        id, title, status, author, published, slug, content, teaser
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
            null, {title}, {status}, {author}, {published}, {slug}, {content}, {excerpt}
          )
        """
      ).on(
        'title -> post.title,
        'status -> post.status,
        'author -> post.author,
        'published -> post.published,
        'slug -> post.slug,
        'content -> post.content,
        'excerpt -> post.excerpt
      ).executeUpdate()
    }
 }

  def update(post: BlogPost, id: Long) = {
//Update an existing blog post
//    val post.id = id
    DB.withConnection { implicit connection =>
      SQL(
        """
          update blogpost
          set title = {title}, status = {status}, author = {author}, published = {published}, slug = {slug}, content = {content}, excerpt = {excerpt}
          where blogpost.id = {id}
        """
      ).on(
        'id -> id,
        'title -> post.title,
        'status -> post.status,
        'author -> post.author,
        'published -> post.published,
        'slug -> post.slug,
        'content -> post.content,
        'excerpt -> post.excerpt
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

  // Retrieve a page of posts
  def findPageOfPosts(page: Int = 0, pageSize: Int = 10): Page[(BlogPost)] = {
    val offset = pageSize * page
    DB.withConnection { implicit connection =>
      val blogPosts = SQL(
        """
          select * from blogpost order by published desc
          limit {pageSize} offset {offset}
        """
      ).on(
        'pageSize -> pageSize,
        'offset -> offset
      ).as(BlogPost.simple *)

      val totalRows = SQL(
        """
          select count(*) from blogpost
        """
      ).as(scalar[Long].single)

      Page(blogPosts, page, offset, totalRows)
    }
  }

}
