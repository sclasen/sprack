resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.0.1")

resolvers += "spray repo" at "http://repo.spray.io"

addSbtPlugin("io.spray" % "sbt-revolver" % "0.6.2")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.8")
