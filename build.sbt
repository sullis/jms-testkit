import xerial.sbt.Sonatype._

name := "jms-testkit"

organization := "io.github.sullis"

scalaVersion := "2.12.7"

crossScalaVersions := Seq(scalaVersion.value)

scalacOptions += "-target:jvm-1.8"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

val activeMqVersion = "5.15.8"

libraryDependencies ++= Seq(
  "org.apache.activemq" % "activemq-broker"   % activeMqVersion,
  "org.apache.activemq" % "activemq-client"   % activeMqVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.testng" % "testng" % "6.10" % Test,
  "com.google.guava" % "guava" % "27.0.1-jre" % Test
)

useGpg := true

releaseCrossBuild := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

publishMavenStyle := true

publishTo := sonatypePublishTo.value

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/sullis/jms-testkit"))

pomExtra := (
  <scm>
    <url>https://github.com/sullis/jms-testkit.git</url>
    <connection>scm:git:git@github.com:sullis/jms-testkit.git</connection>
  </scm>
  <developers>
    <developer>
      <id>sullis</id>
      <name>Sean C. Sullivan</name>
      <url>https://github.com/sullis</url>
    </developer>
  </developers>
)

