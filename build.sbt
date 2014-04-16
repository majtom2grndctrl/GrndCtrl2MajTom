import net.litola.SassPlugin

name := "GrndCtrl2MajTom"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "org.webjars" %% "webjars-play" % "2.2.2", 
  "org.webjars" % "jquery" % "1.11.0",
  "org.webjars" % "modernizr" % "2.6.2-1",
  "com.typesafe.play" %% "play-slick" % "0.6.0.1"
)

// enable improved (experimental) incremental compilation algorithm called "name hashing"
//incOptions := incOptions.value.withNameHashing(true)

scalacOptions ++= Seq("-feature")

play.Project.playScalaSettings ++ SassPlugin.sassSettings
