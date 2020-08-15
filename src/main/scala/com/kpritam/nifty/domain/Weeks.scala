package com.kpritam.nifty.domain

import java.nio.file.Path

import com.kpritam.nifty.domain.NiftyFile.FormatErrors
import com.kpritam.nifty.syntax.EitherExt.ListEitherOps
import com.kpritam.nifty.utils.Csv

case class Weeks private (weeks: List[Week]) {
  def calculateStartEndPriceDiff(startTime: String, endTime: String): Either[List[String], List[ClosingPriceDiff]] =
    weeks.map(_.calcClosingPriceDiff(startTime, endTime)).sequence.left.map(_.flatten)
}

object Weeks {
  def from(folder: Path): Either[FormatErrors, Weeks] =
    allCsvFiles(folder).map(files => new Weeks(files.groupBy(_.weekNo).map(t => Week(t._1, t._2)).toList))

  private def allCsvFiles(folder: Path): Either[FormatErrors, List[NiftyFile]] = Csv.walk(folder).map(NiftyFile(_)).sequence
}
