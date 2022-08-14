name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.13.8"

lazy val akkaVersion = "2.6.14"

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed"           % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"                % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed"     % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit"               % akkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "3.0.4",
  "ch.qos.logback"    % "logback-classic"             % "1.2.10",
  // optional, if you want to add tests
  "com.typesafe.akka" %% "akka-actor-testkit-typed"   % akkaVersion,
  "org.scalatest"     %% "scalatest"                  % "3.2.9"
)