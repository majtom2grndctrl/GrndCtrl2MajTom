package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import anorm._
//import controllers.BlogPosts

object ApplicationSpec extends PlaySpecification {

  import models._

  def dateHelper(str: String): java.util.Date = new java.text.SimpleDateFormat("MM/dd/yyyy").parse(str)

  def populateUser() = User.create(User(NotAssigned, "f@ke.com", "Test", "User", "secret"))
  def populateBlogPost() = {
    populateUser()
    BlogPost.create(
      BlogPost(
        NotAssigned, //id
        "Hello World", //title
        "published", //status
         Id(1), //author
        dateHelper("01/13/2013"), //published
        "hello-world", //slug
        """<p>This is just a test entry. Please delete it by logging in to the backend.</p>""", //content
        Some("""This is just a test entry. Please delete it by logging in to the backend."""
        )//excerpt
      )
    )
  }

  "Application" should {
    "Retrieve blog index" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        populateBlogPost()
        val blogIndex = controllers.BlogPosts.index(0)(FakeRequest())
        status(blogIndex) must equalTo(OK)
        contentType(blogIndex) must beSome("text/html")
        contentAsString(blogIndex) must contain(SitePrefs.name)
      }
    }

    "Retrieve single blog post" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        populateBlogPost()
        val post = controllers.BlogPosts.single("hello-world")(FakeRequest())
        status(post) must equalTo(OK)
        contentType(post) must beSome("text/html")
        contentAsString(post) must contain("Hello World")
      }
    }

    "Retrieve single page" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
//        populateBlogPost()
        val page = controllers.Pages.display("/")(FakeRequest())
        status(page) must equalTo(OK)
        contentType(page) must beSome("text/html")
        contentAsString(page) must contain("Welcome to")
      }
    }

      "Deny access to edit page" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        populateBlogPost()
        val dashboard = route(FakeRequest(GET, "/manage")).get
        status(dashboard) must equalTo(303)
      }
    }

 }

}