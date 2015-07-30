assemblyJarName in assembly := "statisticApp.jar"

mainClass in assembly := Some("cn.edu.zju.king.streaming.StatisticApp")

test in assembly := {}

//assemblyOutputPath in assembly := new sbt.File("dist/")

assemblyOption in assembly := (assemblyOption in assembly).value.copy(cacheOutput = false)

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName == "unused-1.0.0.jar"}
}