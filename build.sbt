import org.typelevel.sbt.gha.WorkflowStep.Run
import org.typelevel.sbt.gha.WorkflowStep.Sbt

val scala213Version = "2.13.16"
val scala3Version = "3.3.6"

ThisBuild / githubOwner := "igor-ramazanov-typelevel"
ThisBuild / githubRepository := "aws-regions"

ThisBuild / githubWorkflowPublishPreamble := List.empty
ThisBuild / githubWorkflowUseSbtThinClient := true
ThisBuild / githubWorkflowPublish := List(
  Run(
    commands = List("echo \"$PGP_SECRET\" | gpg --import"),
    id = None,
    name = Some("Import PGP key"),
    env = Map("PGP_SECRET" -> "${{ secrets.PGP_SECRET }}"),
    params = Map(),
    timeoutMinutes = None,
    workingDirectory = None
  ),
  Sbt(
    commands = List("+ publish"),
    id = None,
    name = Some("Publish"),
    cond = None,
    env = Map("GITHUB_TOKEN" -> "${{ secrets.GB_TOKEN }}"),
    params = Map.empty,
    timeoutMinutes = None,
    preamble = true
  )
)
ThisBuild / gpgWarnOnFailure := false

inThisBuild(
  Seq(
    crossScalaVersions := Seq(scala213Version, scala3Version),
    developers := List(tlGitHubDev("vlovgr", "Viktor Rudebeck")),
    githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17")),
    githubWorkflowTargetBranches := Seq("**"),
    licenses := Seq(License.Apache2),
    organization := "com.magine",
    organizationName := "Magine Pro",
    scalaVersion := scala3Version,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    startYear := Some(2025),
    tlBaseVersion := "1.0",
    tlCiHeaderCheck := true,
    tlCiScalafixCheck := true,
    tlCiScalafmtCheck := true,
    tlFatalWarnings := true,
    tlJdkRelease := Some(8),
    tlUntaggedAreSnapshots := false,
    versionScheme := Some("early-semver")
  )
)

lazy val root = tlCrossRootProject
  .aggregate(core, circe, ciris)

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .in(file("modules/core"))
  .settings(
    name := "aws-regions",
    publishTo := githubPublishTo.value,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
  )

lazy val circe = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .in(file("modules/circe"))
  .settings(
    name := "aws-regions-circe",
    libraryDependencies += "io.circe" %%% "circe-core" % "0.14.13",
    publishTo := githubPublishTo.value,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
  )
  .dependsOn(core)

lazy val ciris = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .in(file("modules/ciris"))
  .settings(
    name := "aws-regions-ciris",
    libraryDependencies += "is.cir" %%% "ciris" % "3.9.0-M1",
    publishTo := githubPublishTo.value,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
  )
  .dependsOn(core)
