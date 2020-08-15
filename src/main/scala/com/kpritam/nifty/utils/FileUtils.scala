package com.kpritam.nifty.utils

import java.io.File
import java.nio.file.{Files, Path, Paths}

import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.Using

object FileUtils {
  def walk(folder: Path): List[Path] = Files.walk(Paths.get(folder.toString)).iterator().asScala.toList

  def read(path: Path): List[String] =
    Using.resource(io.Source.fromFile(new File(path.toString)))(_.getLines.toList.drop(1))
}
