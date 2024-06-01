enablePlugins(Sonatype)

name := "jms-testkit"

organization := "io.github.sullis"

scalaVersion := "2.12.19"

crossScalaVersions := Seq(scalaVersion.value, "2.11.12", "2.13.14", "3.4.2")

scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) =>
          Seq("-source:3.0-migration", "-explain", "-explain-types")
        case _ =>
          Nil
      }
    }

javacOptions ++= Seq("-source", "11", "-target", "11")

parallelExecution := true

val activeMqArtemisVersion = "2.33.0"

libraryDependencies ++= Seq(
  "org.apache.activemq" % "artemis-server"   % activeMqArtemisVersion,
  "org.apache.activemq" % "artemis-jakarta-client"   % activeMqArtemisVersion,
  "org.apache.activemq" % "artemis-jakarta-server"   % activeMqArtemisVersion,
  "jakarta.jms" % "jakarta.jms-api" % "3.1.0",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test,
  "org.testng" % "testng" % "7.10.2" % Test,
  "com.google.guava" % "guava" % "33.2.1-jre" % Test,
  "ch.qos.logback" % "logback-classic" % "1.5.6" % Test,
  "ch.qos.logback" % "logback-core" % "1.5.6" % Test
)

updateOptions := updateOptions.value.withGigahorse(false)

Test / publishArtifact := false

pomIncludeRepository := { _ => false }

licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/sullis/jms-testkit"))

pomExtra := (
  <developers>
    <developer>
      <id>sullis</id>
      <name>Sean C. Sullivan</name>
      <url>https://github.com/sullis</url>
    </developer>
  </developers>
)
