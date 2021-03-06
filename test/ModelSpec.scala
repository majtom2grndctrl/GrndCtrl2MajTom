package test

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import anorm._
import anorm.SqlParser._

import java.util.{Date}
import java.text.SimpleDateFormat

@RunWith(classOf[JUnitRunner])
class ModelSpec extends Specification {

  import models._
  import models.BlogPost._

  // -- Date helper
  def dateHelper(str: String): java.util.Date = new java.text.SimpleDateFormat("MM/dd/yyyy").parse(str)
  def dbDateHelper(date: Date, str: String) = new SimpleDateFormat("MM/dd/yyy").format(date) == str

  def createUser() = User.create(User(NotAssigned, "f@ke.com", "Test", "User", "secret"))

  def populateBlogPost() = {
    createUser()
    BlogPost.create(
      BlogPost(
        NotAssigned, //id
        "Hello World", //title
        "published", //status
         Id(1), //author
        dateHelper("01/13/2013"), //published
        "hello-world", //slug
        """<p>This is just a test entry. Please delete it by logging in to the backend.</p>""", //content
        Some("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>"""
        ) //excerpt
      )
    )
  }

  def populatePage() = {
    createUser()
    Page.create(
      Page(
        NotAssigned, //id
        "Hello World", //title
        "published", //status
        "hello-world", //slug
        """<p>This is just a test entry. Please delete it by logging in to the backend.</p>""", //content
        Some("""This is just a test entry. Please delete it by logging in to the backend."""),//description
        Some("meta keywords")// keywords
      )
    )
  }

  "Models" should {
    "save and retrieve a new User" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser()
        val Some(user) = User.findById(1)
        user.id must equalTo(Id(1))
        user.email must equalTo("f@ke.com")
        user.firstName must equalTo("Test")
        user.lastName must equalTo("User")
        user.password must equalTo("secret")
      }
    }
    "save and retrieve a new BlogPost" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        populateBlogPost()

        val Some(post) = BlogPost.findNewestSaved(Id(1), "hello-world")

          post.id must equalTo(Id(1))
          post.title must equalTo("Hello World")
          post.status must equalTo("published")
          post.slug must equalTo("hello-world")
          post.content must equalTo("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>""")
          post.excerpt must equalTo(Some("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>"""))
      }
    }
    "save and update a new BlogPost" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        populateBlogPost()

        BlogPost.update(
          BlogPost(
            NotAssigned, //id
            "Hello World Updated", //title
            "published", //status
            Id(1), //author
            dateHelper("01/14/2013"), //published
            "hello-world-updated", //slug
            """<p>This is just an updated test entry. Please delete it by logging in to the backend.</p>""", //content
            Some("""<p>This is just an updated test entry. Please delete it by logging in to the backend.</p>"""//excerpt
            )
          ),
          1: Long
        )
        val Some(post) = BlogPost.findById(1)
        post.author must equalTo(Id(1))
        post.id must equalTo(Id(1))
        post.title must equalTo("Hello World Updated")
        post.status must equalTo("published")
        post.slug must equalTo("hello-world-updated")
        post.content must equalTo("""<p>This is just an updated test entry. Please delete it by logging in to the backend.</p>""")
        post.excerpt must equalTo(Some("""<p>This is just an updated test entry. Please delete it by logging in to the backend.</p>"""))
      }      
    }

    "save two Blog Posts and delete one" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        populateBlogPost()

        BlogPost.create(
          BlogPost(
            NotAssigned, //id
            "Hello Again", //title
            "published", //status
            Id(1), //author
            dateHelper("01/14/2013"), //published
            "hello-again", //slug
            """<p>This is just a test entry. Please delete it by logging in to the backend.</p>""", //content
            Some("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>"""
            ) //excerpt
          )
        )

        BlogPost.delete(2)

        val Some(post) = BlogPost.findById(1)

          post.id must equalTo(Id(1))
          post.title must equalTo("Hello World")
          post.status must equalTo("published")
          post.slug must equalTo("hello-world")
          post.content must equalTo("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>""")
          post.excerpt must equalTo(Some("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>"""))

      }
    }

    "save and retrieve a new Page" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        populatePage()

        val Some(page) = Page.findBySlug("hello-world")
        page.id must equalTo(Id(3))
        page.title must equalTo("Hello World")
        page.status must equalTo("published")
        page.slug must equalTo("hello-world")
        page.content must equalTo("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>""")
        page.description must equalTo(Some("""This is just a test entry. Please delete it by logging in to the backend."""))
        page.keywords must equalTo(Some("meta keywords"))
      }
    }

    "save and retrieve a new Page" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        populatePage()

        val Some(page) = Page.findBySlug("hello-world")
        page.id must equalTo(Id(3))
        page.title must equalTo("Hello World")
        page.status must equalTo("published")
        page.slug must equalTo("hello-world")
        page.content must equalTo("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>""")
        page.description must equalTo(Some("""This is just a test entry. Please delete it by logging in to the backend."""))
        page.keywords must equalTo(Some("meta keywords"))
      }
    }

    "save and retrieve the newest Page" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        populatePage()

        val Some(page) = Page.findNewestSaved("hello-world")
        page.id must equalTo(Id(3))
        page.title must equalTo("Hello World")
        page.status must equalTo("published")
        page.slug must equalTo("hello-world")
        page.content must equalTo("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>""")
        page.description must equalTo(Some("""This is just a test entry. Please delete it by logging in to the backend."""))
        page.keywords must equalTo(Some("meta keywords"))
      }      
    }

  }

}