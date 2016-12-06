package controllers

import play.api.mvc._

class Contacts extends Controller {
  def mailto(recipient: String) = Action {
    recipient match {
      case "dhies" => Redirect("mailto:contact-2016@distantlyyours.com")
      case _ => NotFound
    }
  }

}
