package com.kpritam.nifty.domain

import java.nio.file.Path
import java.time.LocalDate

import com.kpritam.nifty.syntax.DateExt.DateOps
import com.kpritam.nifty.syntax.EitherExt.ListEitherOps
import com.kpritam.nifty.utils.{Csv, FileUtils}

case class NiftyFile private (path: Path, date: LocalDate, weekNo: Int) {
  def read: Either[List[String], NiftyFileContent] = FileUtils.read(path).map(InputRow.from).sequence.map(NiftyFileContent)
}

object NiftyFile {
  type FormatErrors = List[String]

  def apply(path: Path): Either[String, NiftyFile] =
    Csv.extractDate(path).map { date => new NiftyFile(path, date, date.niftyWeekNo) }

  implicit val niftyFileOrd: Ordering[NiftyFile] = Ordering.by(_.date)
}
