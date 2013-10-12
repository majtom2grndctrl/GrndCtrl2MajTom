package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import anorm._
//import controllers.BlogPosts

object ApplicationSpec extends PlaySpecification {

  import models._

  def dateHelper(str: String): java.util.Date = new java.text.SimpleDateFormat("MM/dd/yyyy").parse(str)

  def populateDb() = {
    User.create(User(NotAssigned, "f@ke.com", "Test", "User", "secret"))
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
        )//excerpt
      )
    )
  }

  "Application" should {
    "Retrieve blog index" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        populateDb()
        val blogIndex = controllers.BlogPosts.index(0)(FakeRequest())
        status(blogIndex) must equalTo(OK)
        contentType(blogIndex) must beSome("text/html")
        contentAsString(blogIndex) must contain(SitePrefs.name)
      }
    }

    "Deny access to edit page" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        populateDb()
        val dashboard = route(FakeRequest(GET, "/manage")).get
        status(dashboard) must equalTo(303)
      }
    }

 }

}