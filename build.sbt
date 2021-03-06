import sbt.url

val libVersion = new {
  val causeToujours = "0.1.0"
  val kafka         = "0.11.0.0"
  val slf4j         = "1.7.5"

  // formats
  val json4s         = "3.6.1"
  val circe          = "0.9.3"
  val scalaXml       = "1.0.6"
  val avro4s         = "1.9.0"
  val typesafeConfig = "1.3.3"

  // tests
  val scalaTest     = "3.0.5"
  val embeddedKafka = "2.0.0"
}

lazy val zoomAll =
  (project in file("."))
    .dependsOn(core)
    .aggregate(core, integration, bench)
    .settings(commonSettings)

def circe(modules: String*) = modules.map(module ⇒ "io.circe" %% s"circe-$module" % libVersion.circe)

lazy val core =
  (project in file("zoom-core"))
    .settings(commonSettings, publishSettings)
    .settings(
      libraryDependencies += "io.univalence" %% "cause-toujours" % libVersion.causeToujours,
      libraryDependencies ++= Seq(
        "org.apache.kafka" % "kafka-clients" % libVersion.kafka,
        "org.apache.kafka" %% "kafka"        % libVersion.kafka,
        "org.slf4j"        % "slf4j-api"     % libVersion.slf4j,
        "org.slf4j"        % "slf4j-log4j12" % libVersion.slf4j
      ),
      // Formats
      libraryDependencies ++= Seq(
        "org.json4s"             %% "json4s-native" % libVersion.json4s,
        "org.scala-lang.modules" %% "scala-xml"     % libVersion.scalaXml,
        "com.sksamuel.avro4s"    %% "avro4s-core"   % libVersion.avro4s,
        "com.typesafe"           % "config"         % libVersion.typesafeConfig
      ),
      // Circe
      libraryDependencies ++=
        circe("core", "generic", "parser", "generic-extras", "optics"),
      //Test
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % libVersion.scalaTest % Test
      )
    )

lazy val integration =
  (project in file("integration"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest"                % libVersion.scalaTest     % Test,
        "net.manub"     %% "scalatest-embedded-kafka" % libVersion.embeddedKafka % Test
      ),
      parallelExecution := false
    )
    .dependsOn(core)

lazy val bench =
  (project in file("bench"))
    .settings(commonSettings)
    .enablePlugins(JmhPlugin)
    .dependsOn(core)

lazy val metadataSettings =
  Def.settings(
    organization := "io.univalence",
    version      := "0.3-SNAPSHOT",
    description  := "Zoom is an event bus",
    licenses     := Seq("The Apache License, Version 2.0" → url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    developers := List(
      Developer(
        id    = "jwinandy",
        name  = "Jonathan Winandy",
        email = "jonathan@univalence.io",
        url   = url("https://github.com/ahoy-jon")
      ),
      Developer(
        id    = "phong",
        name  = "Philippe Hong",
        email = "philippe@univalence.io",
        url   = url("https://github.com/hwki77")
      ),
      Developer(
        id    = "fsarradin",
        name  = "François Sarradin",
        email = "francois@univalence.io",
        url   = url("https://github.com/fsarradin")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/UNIVALENCE/Zoom"),
        "scm:git:https://github.com/UNIVALENCE/Zoom.git",
        Some(s"scm:git:git@github.com:UNIVALENCE/Zoom.git")
      ))
  )

lazy val scalaSettings =
  Def.settings(
    crossScalaVersions        := Seq("2.11.12", "2.12.6"),
    scalaVersion in ThisBuild := crossScalaVersions.value.find(_.startsWith("2.11")).get,
    scalacOptions := Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", // Specify character encoding used by source files (linked to the previous item).
      "-explaintypes", // Explain type errors in more detail.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      "-language:higherKinds", // Allow higher-kinded types
      "-language:implicitConversions", // Allow definition of implicit functions called views
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
      //    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xfuture", // Turn on future language features.
      "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
      "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
      "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
      "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
      "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
      "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
      "-Xlint:option-implicit", // Option.apply used implicit view.
      "-Xlint:package-object-classes", // Class or object defined in package object.
      "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
      "-Xlint:unsound-match", // Pattern match may not be typesafe.
      "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
      "-Ypartial-unification", // Enable partial unification in type constructor inference
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
      "-Ywarn-numeric-widen" // Warn when numerics are widened.
    )
  )

lazy val publishSettings =
  Def.settings(
    publishMavenStyle := true,
    publishTo         := Some(sonatypeDefaultResolver.value),
    useGpg            := true
  )

lazy val commonSettings =
  Def.settings(
    metadataSettings,
    scalaSettings /*,
       scalafmtOnCompile in ThisBuild := true,
       scalafmtTestOnCompile in ThisBuild := true
   */
  )

addCommandAlias("bench", ";project bench;jmh:run")
addCommandAlias("quick-bench", ";project bench;jmh:run -i 1 -wi 0 -f1 -t1")
