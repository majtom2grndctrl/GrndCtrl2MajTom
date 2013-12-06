name := "GrndCtrl2MajTom"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "org.webjars" %% "webjars-play" % "2.2.0", 
  "org.webjars" % "jquery" % "1.10.2-1",
  "org.webjars" % "modernizr" % "2.6.2-1"
)     

play.Project.playScalaSettings
