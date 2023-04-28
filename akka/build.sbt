ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Akka"
  )
//val akkaversion = "2.3.8"
//
//libraryDependencies ++= Seq("com.typesafe.akka" %% "akkaactor" % akkaversion,
//                            "com.typesafe.akka" %% "akkatestkit" % akkaversion,
//                             "org.scalatest" %% "scalatest" % "3.2.15")

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % "2.8.0"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-testkit" % "2.8.0"

libraryDependencies +=
  "org.scalatest" %% "scalatest" % "3.2.15"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-stream" % "2.8.0"

libraryDependencies ++= Seq(

  "com.typesafe" % "config" % "1.4.2"

)

val AkkaVersion = "2.7.0"
libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "3.0.0",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion
)

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"

libraryDependencies += "org.apache.commons" % "commons-csv" % "1.10.0"


