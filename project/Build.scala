import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "GrndCtrl2MajTom"
    val appVersion      = "0.1"

    val appDependencies = Seq(
      jdbc, anorm, "mysql" % "mysql-connector-java" % "5.1.27"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here
    )

}
