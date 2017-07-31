lazy val akkaHttpVersion = "10.0.9"
lazy val akkaVersion    = "2.5.3"



lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.12.2"
    )),
    resolvers += Resolver.jcenterRepo,
    name := "akka-user-management",
    libraryDependencies ++= Seq(
      // Akka Http
      "com.typesafe.akka" %% "akka-http"         % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"       % akkaVersion,
      // Akka Persistence
      "com.typesafe.akka"           %% "akka-persistence"  % akkaVersion,
      "org.iq80.leveldb"            % "leveldb"          % "0.7",
      "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8",
      // Test
      "com.typesafe.akka"           %% "akka-http-testkit" % akkaHttpVersion % Test,
      "org.scalatest"               %% "scalatest"         % "3.0.1"         % Test,
      "com.github.dnvriend"         %% "akka-persistence-inmemory" % "2.5.1.1"
    )
  )
