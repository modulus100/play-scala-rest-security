import sbt.Resolver

name := """play-scala-rest-security"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.jcenterRepo
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalaVersion := "2.12.2"

val silhouetteVer = "5.0.0"
val swaggerUIVersion = "3.6.1"

lazy val silhouetteLib = Seq(
  "com.mohiva" %% "play-silhouette" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-persistence" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-testkit" % silhouetteVer % "test"
)

unmanagedResourceDirectories in Test += (baseDirectory.value / "target/web/public/test")

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies ++= Seq(
  jdbc,
  "mysql" % "mysql-connector-java" % "5.1.41",
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.7-play26",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.iheart" %% "ficus" % "1.4.3",
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
  "io.swagger" %% "swagger-play2" % "1.6.1-SNAPSHOT",
  "org.webjars" % "swagger-ui" % swaggerUIVersion
) ++ silhouetteLib

//enablePlugins(DockerPlugin)