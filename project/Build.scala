import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "GrndCtrl2MajTom"
    val appVersion      = "0.1"

    val appDependencies = Seq(
      jdbc, anorm, "mysql" % "mysql-connector-java" % "5.1.27"/*,
      "com.typesafe.play" %% "play-slick" % "0.5.0.8" Holding off on Slick for now */
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here
      scalacOptions += "-feature"
    )

}
