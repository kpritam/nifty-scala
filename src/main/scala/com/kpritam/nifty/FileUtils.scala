package com.kpritam.nifty

import java.nio.file.{Files, Path, Paths}

import scala.jdk.CollectionConverters.IteratorHasAsScala

object FileUtils {

  def walk(folder: Path): List[Path] = Files.walk(Paths.get(folder.toString)).iterator().asScala.toList

}
