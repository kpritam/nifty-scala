package com.kpritam.nifty.domain

import java.time.LocalDate

import com.kpritam.nifty.utils.DateUtils

case class InputRow(
    ticker: String,
    date: LocalDate,
    time: String,
    open: String,
    high: String,
    low: String,
    close: String,
    volume: String,
    openInterest: String
) {
  def write(diff: Double) = s"$ticker,$date,$time,$open,$high,$low,$close,$volume,$openInterest,$diff"
}

object InputRow {
  def from(line: String): Either[String, InputRow] = {
    val arr = line.split(",").map(_.trim)
    if (arr.length == 9) {
      DateUtils
        .parse(arr(1))
        .map(InputRow(arr(0), _, arr(2), arr(3), arr(4), arr(5), arr(6), arr(7), arr(8)))
    }
    else Left(s"Expected total 9 columns but found ${arr.length}, line: " + line)
  }
}
