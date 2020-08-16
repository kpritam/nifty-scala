package com.kpritam.nifty.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Try

object DateUtils {
  private val dateFormats = List("dd/MM/yyyy", "ddMMyyyy")
  private val formatters  = dateFormats.map(DateTimeFormatter.ofPattern)

  def parse(date: String): Either[String, LocalDate] = {
    def loop(formatters: List[DateTimeFormatter]): Option[LocalDate] =
      formatters match {
        case formatter :: rest => Try(LocalDate.parse(date, formatter)).fold(_ => loop(rest), Some(_))
        case Nil               => None
      }

    loop(formatters).toRight(s"Unable to parse date: $date, supported date formats are: ${dateFormats.mkString("|")}")
  }

}
