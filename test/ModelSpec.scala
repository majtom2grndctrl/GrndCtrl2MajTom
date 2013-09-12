package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import anorm._

import java.util.{Date}
import java.text.SimpleDateFormat

class ModelSpec extends Specification {

  import models._

  // -- Date helper
  def dateHelper(str: String): java.util.Date = new java.text.SimpleDateFormat("MM/dd/yyyy").parse(str)
  def dbDateHelper(date: Date, str: String) = new SimpleDateFormat("MM/dd/yyy").format(date) == str

  "Models" should {
    "save and retrieve a new User" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        User.create(User(NotAssigned, "f@ke.com", "Test", "User", "secret"))
        val Some(user) = User.findById(1000)
        user.id must equalTo(Id(1000))
        user.email must equalTo("f@ke.com")
        user.firstName must equalTo("Test")
        user.lastName must equalTo("User")
        user.password must equalTo("secret")
      }
    }
    "save and retrieve a new BlogPost" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        User.create(User(NotAssigned, "f@ke.com", "Test", "User", "secret"))
        BlogPost.create(
          BlogPost(
            NotAssigned, //id
            "Hello World", //title
            "published", //status
             Id(1000), //author
            dateHelper("01/13/2013"), //published
            Some("hello-world"), //slug
            """<p>This is just a test entry. Please delete it by logging in to the backend.</p>""", //content
            Some("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>"""
            ) //excerpt
          )
        )

        val Some(post) = BlogPost.findById(1000)
        post.author must equalTo(Id(1000))
        post.id must equalTo(Id(1000))
        post.title must equalTo("Hello World")
        post.status must equalTo("published")
        post.slug must equalTo(Some("hello-world"))
        post.content must equalTo("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>""")
        post.excerpt must equalTo(Some("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>"""))
      }
    }
    "save and update a new BlogPost" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        User.create(User(NotAssigned, "f@ke.com", "Test", "User", "secret"))
        BlogPost.create(
          BlogPost(
            NotAssigned, //id
            "Hello World", //title
            "published", //status
             Id(1000), //author
            dateHelper("01/13/2013"), //published
            Some("hello-world"), //slug
            """<p>This is just a test entry. Please delete it by logging in to the backend.</p>""", //content
            Some("""<p>This is just a test entry. Please delete it by logging in to the backend.</p>"""
            )//excerpt
          )
        )

        BlogPost.update(
          BlogPost(
            NotAssigned, //id
            "Hello World Updated", //title
            "published", //status
            Id(1000), //author
            dateHelper("01/14/2013"), //published
            Some("hello-world-updated"), //slug
            """<p>This is just an updated test entry. Please delete it by logging in to the backend.</p>""", //content
            Some("""<p>This is just an updated test entry. Please delete it by logging in to the backend.</p>"""//excerpt
            )
          ),
          1000: Long
        )
        val Some(post) = BlogPost.findById(1000)
        post.author must equalTo(Id(1000))
        post.id must equalTo(Id(1000))
        post.title must equalTo("Hello World Updated")
        post.status must equalTo("published")
        post.slug must equalTo(Some("hello-world-updated"))
        post.content must equalTo("""<p>This is just an updated test entry. Please delete it by logging in to the backend.</p>""")
        post.excerpt must equalTo(Some("""<p>This is just an updated test entry. Please delete it by logging in to the backend.</p>"""))
      }      
    }
  }

}