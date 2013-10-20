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
  slug: String,
  content: String,
  excerpt: Option[String]
)

case class BlogPostsPage[A](items: Seq[A], BlogPostsPage: Int, offset: Long, total: Long) {
  lazy val prev = Option(BlogPostsPage -1).filter(_ >= 0 )
  lazy val next = Option(BlogPostsPage +1).filter(_ => (offset + items.size) < total)
}

object BlogPost {
  val simple = {
    get[Pk[Long]]("blogPost.id") ~
    get[String]("blogPost.title") ~
    get[String]("blogPost.status") ~
    get[Pk[Long]]("blogPost.author") ~
    get[Date]("blogPost.published") ~
    get[String]("blogPost.slug") ~
    get[String]("blogPost.content") ~
    get[Option[String]]("blogPost.excerpt") map {
      case id~title~status~author~published~slug~content~teaser => BlogPost(
        id, title, status, author, published, slug, content, teaser
      )
    }
  }

  val withAuthor = BlogPost.simple ~ (User.simple) map {
    case post~user => (post, user)
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
  def findNewestSaved(authorId: Pk[Long], slug: String): Option[BlogPost] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          select * from blogpost
          where blogpost.slug = {slug}
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

}
