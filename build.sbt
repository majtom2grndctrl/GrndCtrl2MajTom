name := """GrndCtrl2MajTom"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "org.webjars" %% "webjars-play" % "2.3.0", 
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "modernizr" % "2.6.2-1"
)

seq(sassSettings : _*)

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

LessKeys.compress in Assets := true