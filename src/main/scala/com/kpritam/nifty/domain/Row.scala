package com.kpritam.nifty.domain

import java.util.Date

import com.kpritam.nifty.utils.DateUtils

case class Row(
    ticker: String,
    date: Date,
    time: String,
    open: String,
    high: String,
    low: String,
    close: String,
    volume: String,
    openInterest: String
)

object Row {
  def from(line: String): Either[String, Row] = {
    val arr = line.split(",").map(_.trim)
    if (arr.length == 9) {
      DateUtils
        .parse(arr(1))
        .map(Row(arr(0), _, arr(2), arr(3), arr(4), arr(5), arr(6), arr(7), arr(8)))
    }
    else Left(s"Expected total 9 columns but found ${arr.length}, line: " + line)
  }
}
