name := "ScalaCrawler"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= {
  Seq(
    "org.jsoup"         % "jsoup"                                 % "1.8+"
  )
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.9" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "commons-validator" % "commons-validator" % "1.5+",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0-RC2",
  "com.typesafe.akka" %% "akka-http-core" % "10.1.0-RC2"

)
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.5.0"
libraryDependencies += "javax.ws.rs" % "javax.ws.rs-api" % "2.0"
libraryDependencies += "org.glassfish.jersey.core" % "jersey-client" % "2.22.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "com.github.ptnplanet" % "Java-Naive-Bayes-Classifier" % "1.0.7"
