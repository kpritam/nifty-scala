package com.kpritam.nifty

import java.nio.file.Path
import java.util.Date

object Csv {
  private val ext = ".csv"

  def isCsv(path: Path): Boolean = path.toString.endsWith(ext)

  def extractDateFromPath(path: Path): Either[String, Date] = {
    val fileName   = path.getFileName.toString
    val dateStrOpt = fileName.replace(ext, "").split("_").lastOption
    dateStrOpt
      .toRight(s"$fileName should end with date string in the format of name_date.csv")
      .flatMap(DateParser.parse)
  }

}
