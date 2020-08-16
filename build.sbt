scalaVersion := "2.13.3"
name := "nifty-scala"
organization := "com.kpritam"
version := "1.0"

libraryDependencies += "dev.zio"      %% "zio"                % "1.0.0"
libraryDependencies += "dev.zio"      %% "zio-streams"        % "1.0.0"
libraryDependencies += "com.nrinaudo" %% "kantan.csv-java8"   % "0.6.1"
libraryDependencies += "com.nrinaudo" %% "kantan.csv-generic" % "0.6.1"
