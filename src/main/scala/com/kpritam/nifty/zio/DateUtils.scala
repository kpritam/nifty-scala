package com.kpritam.nifty.zio

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import zio.Task

import scala.util.Try

object DateUtils {
  private val format    = "ddMMyyyy"
  private val formatter = DateTimeFormatter.ofPattern(format)

  def parse(date: String): Task[LocalDate] = Task.fromTry(Try(LocalDate.parse(date, formatter)))

}
