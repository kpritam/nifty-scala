package com.kpritam.nifty.domain

case class Week private (no: Int, files: List[NiftyFile]) {
  private lazy val sorted      = files.sorted
  def start: Option[NiftyFile] = sorted.headOption
  def end: Option[NiftyFile]   = sorted.lastOption

  def closingPriceOn(file: NiftyFile, time: String): Either[List[String], Double] =
    file.read.flatMap(_.closePriceAt(time).toRight(List(s"CLose price not found at time: $time in file ${file.path}")))

  def closingPriceOnStartDay(time: String): Either[List[String], Double] =
    start.toRight(List(s"Start date not present for $this")).flatMap(closingPriceOn(_, time))

  def closingPriceOnEndDay(time: String): Either[List[String], Double] =
    end.toRight(List(s"End date not present for $this")).flatMap(closingPriceOn(_, time))

  def calcClosingPriceDiff(startTime: String, endTime: String): Either[List[String], ClosingPriceDiff] =
    closingPriceOnEndDay(endTime).flatMap(e => closingPriceOnStartDay(startTime).map(s => ClosingPriceDiff(s, e)))
}

object Week {
  implicit val weekOrd: Ordering[Week] = Ordering.by(_.no)
}
