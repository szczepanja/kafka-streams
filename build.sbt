ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "kafka-streams"
  )

libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "3.2.0"
libraryDependencies += "org.apache.kafka" % "kafka-streams-test-utils" % "3.2.0" % Test
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.12"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % Test

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.36"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.36"
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"

enablePlugins(DockerPlugin)

docker / dockerfile := {
  val jarFile: File = (Compile / packageBin / sbt.Keys.`package`).value
  val classpath = (Compile / managedClasspath).value
  val mainclass = (Compile / packageBin / mainClass).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget
  new Dockerfile {
    from("openjdk:8-jre")
    add(classpath.files, "/app/")
    add(jarFile, jarTarget)
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}

