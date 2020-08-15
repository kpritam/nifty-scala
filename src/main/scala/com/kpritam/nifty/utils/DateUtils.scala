package com.kpritam.nifty.utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import scala.util.Try

object DateUtils {
  private val dateFormats = List("dd/mm/yyyy", "ddmmyyyy")
  private val formatters  = dateFormats.map(new SimpleDateFormat(_))

  def parse(date: String): Either[String, Date] = {
    def loop(formatters: List[SimpleDateFormat]): Option[Date] =
      formatters match {
        case formatter :: rest => Try(formatter.parse(date)).fold(_ => loop(rest), Some(_))
        case Nil               => None
      }

    loop(formatters).toRight(s"Unable to parse date: $date, supported date formats are: ${dateFormats.mkString("|")}")
  }

}
