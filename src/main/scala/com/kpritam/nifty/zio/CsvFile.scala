package com.kpritam.nifty.zio

import java.nio.file.Path
import java.time.LocalDate

import zio.Task

import scala.util.Try

case class CsvFile(date: LocalDate, path: Path, data: List[Row])

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
    extractDate(file).flatMap(date => FileUtils.readCsv(file).map(CsvFile(date, file, _)))

  implicit val ord: Ordering[CsvFile] = Ordering.by(_.date)
}
