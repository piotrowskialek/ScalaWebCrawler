name := "ScalaCrawler"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= {
  Seq(
    "org.jsoup"         % "jsoup"                                 % "1.8+"
  )
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test,

  "commons-validator" % "commons-validator" % "1.5+"

)

libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "10.0.6"

libraryDependencies += "org.mongodb" %% "casbah" % "3.1.1"
libraryDependencies += "javax.ws.rs" % "javax.ws.rs-api" % "2.0"
libraryDependencies += "org.glassfish.jersey.core" % "jersey-client" % "2.22.2"
