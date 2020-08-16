package com.kpritam.nifty.zio

import java.io.File
import java.nio.file.{Files, Path, Paths}

import zio.stream._
import zio.{IO, Task, UIO}

import scala.io.{BufferedSource, Source}
import scala.jdk.CollectionConverters.IteratorHasAsScala

object FileUtils {
  private val ext = ".csv"

  def isCsv(path: Path): Boolean = path.toString.endsWith(ext)

  def walk(folder: String): Task[List[Path]] =
    Task.effect(Files.walk(Paths.get(folder)).iterator().asScala.toList)

  def walkCsv(folder: String): Task[List[Path]] = walk(folder).map(_.filter(isCsv))

  private def closeSource(source: BufferedSource) = UIO(source.close())

  def readCsv(name: Path): Stream[Throwable, Row] =
    Stream
      .bracket(IO(Source.fromFile(new File(name.toString))))(closeSource)
      .flatMap(Row.rowReader)

}
