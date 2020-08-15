package com.kpritam.nifty

import java.io.File
import java.nio.file.{Path, Paths}

import com.kpritam.nifty.FileInfo.{listAllFilesGroupedByWeek, startAndEndDayOfWeek}

import scala.util.Using

object Main extends App {
  val folder = Paths.get("/Users/pritamkadam/Downloads/APR-2020")

  val files = listAllFilesGroupedByWeek(folder)

  val diffs = files
    .map(startAndEndDayOfWeek)
    .map(_.map {
      case (i, List(start, end)) =>
        val startClosePrice = closePriceAt("9:30", start)
        val endClosePrice   = closePriceAt("14:30", end)
        val diff            = endClosePrice.flatMap(e => startClosePrice.map(e - _)).map(_.toString)
        (i, List(start.path.toString, end.path.toString) ++ diff.toList)
      case (i, _) => (i, List.empty)
    })

  println(diffs.map(_.mkString("\n")).left.map(_.mkString("\n")))

  def read(path: Path): List[String] =
    Using.resource(io.Source.fromFile(new File(path.toString)))(_.getLines.toList.drop(1))

  def closePriceAt(at: String, fileInfo: FileInfo) =
    read(fileInfo.path).map(Row.from).find(_.time.startsWith(at)).map(_.close)

}
