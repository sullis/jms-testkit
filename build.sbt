enablePlugins(Sonatype)

name := "jms-testkit"

organization := "io.github.sullis"

scalaVersion := "2.12.17"

crossScalaVersions := Seq(scalaVersion.value, "2.11.12", "2.13.10", "3.2.2")

scalacOptions += "-target:jvm-1.8"

scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) =>
          Seq("-source:3.0-migration", "-explain", "-explain-types")
        case _ =>
          Nil
      }
    }

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

parallelExecution := true

val activeMqVersion = "5.16.5"

libraryDependencies ++= Seq(
  "org.apache.activemq" % "activemq-broker"   % activeMqVersion,
  "org.apache.activemq" % "activemq-client"   % activeMqVersion,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "org.testng" % "testng" % "7.5" % Test,
  "com.google.guava" % "guava" % "31.1-jre" % Test,
  "ch.qos.logback" % "logback-classic" % "1.2.12" % Test,
  "ch.qos.logback" % "logback-core" % "1.2.12" % Test
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
