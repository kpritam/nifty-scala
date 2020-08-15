package com.kpritam.nifty

import java.text.SimpleDateFormat
import java.util.Date

import scala.util.Try

object DateParser {
  private val dateFmt                  = "ddmmyyyy"
  private def format: SimpleDateFormat = new SimpleDateFormat(dateFmt)

  def parse(str: String): Either[String, Date] =
    Try(format.parse(str)).toEither.left.map(e =>
      s"Unable to parse date str: $str, supported date format is: $dateFmt, reason: ${e.getMessage}"
    )
}
