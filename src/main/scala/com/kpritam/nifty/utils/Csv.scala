package com.kpritam.nifty.utils

import java.nio.file.Path
import java.time.LocalDate
import java.util.Date

object Csv {
  private val ext = ".csv"

  def isCsv(path: Path): Boolean = path.toString.endsWith(ext)

  def walk(folder: Path): List[Path] = FileUtils.walk(folder).filter(isCsv)

  //name format -> GFDLNFO_NIFTY_CONTRACT_07042020.csv
  def extractDate(path: Path): Either[String, LocalDate] = {
    val fileName   = path.getFileName.toString
    val dateStrOpt = fileName.replace(ext, "").split("_").lastOption
    dateStrOpt
      .toRight(s"$fileName should end with date string in the format of name_date.csv")
      .flatMap(DateUtils.parse)
  }

}
