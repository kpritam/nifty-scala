package com.kpritam.nifty.zio

import java.nio.file.Path
import java.time.LocalDate

import zio.{Task, ZIO}

import scala.util.Try

case class CsvFile(date: LocalDate, path: Path, data: Task[List[Row]]) {
  def rowAt(time: String): Task[Row] =
    data
      .map(_.find(_.time.trim.startsWith(time)))
      .someOrFail(new RuntimeException(s"$path does not contain row matching close time $time"))
}

object CsvFile {
  //name format -> GFDLNFO_NIFTY_CONTRACT_07042020.csv
  def extractDate(path: Path): Task[LocalDate] =
    Task
      .fromTry(
        Try {
          val fileName   = path.getFileName.toString
          val dateStrOpt = fileName.replace(".csv", "").split("_").lastOption
          dateStrOpt.getOrElse(
            throw new RuntimeException(s"$fileName should end with date string in the format of name_date.csv")
          )
        }
      )
      .flatMap(DateUtils.parse)

  def from(file: Path): Task[CsvFile] =
    extractDate(file).map(date => CsvFile(date, file, FileUtils.readCsv(file)))

  implicit val ord: Ordering[CsvFile] = Ordering.by(_.date)
}
