import sbt._
import Keys._

  val ScalaVersion = "2.12.4"

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "com.softwaremill.macmemo",
    scalacOptions ++= Seq(),
    crossScalaVersions := Seq("2.11.8", "2.12.4"),
    scalaVersion := ScalaVersion,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )


  lazy val root: Project = (project in file("."))
    .settings(buildSettings)
    .aggregate(macros)

  lazy val macros: Project = (project in file("macros"))
    .settings(buildSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % ScalaVersion,
      "com.google.guava" % "guava" % "13.0.1",
      "com.google.code.findbugs" % "jsr305" % "1.3.9",
      "org.scalatest" %% "scalatest" % "3.0.0" % "test"
      ),
    parallelExecution in Test := false,
    scalacOptions := Seq("-feature", "-deprecation"),
    testOptions in Test += Tests.Setup( () => System.setProperty("macmemo.debug", "true"))
    )
  )


