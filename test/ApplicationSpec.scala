package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import anorm._
import controllers.BlogPosts

class ApplicationSpec extends Specification {
/* Looks like I can't test controllers if authentication is in play. Will have to wait for 2.1 release.
  import models._

  def dateHelper(str: String): java.util.Date = new java.text.SimpleDateFormat("MM/dd/yyyy").parse(str)

  "Application" should {
    "Retrieve a blog post and display it in the editor" in {
      
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

        val result = controllers.BlogPosts.edit(1000)(FakeRequest())
//        val Some(result) = routeAndCall(FakeRequest(GET, "/manage/blogPosts/edit/1000"))

        status(result) must equalTo(OK)
//        contentAsString(result) must contain("Title")

      }      
    }
  }
*/
}