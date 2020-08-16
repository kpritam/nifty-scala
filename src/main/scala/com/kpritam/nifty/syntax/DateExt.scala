package com.kpritam.nifty.syntax

import java.time.LocalDate
import java.util.Calendar

object DateExt {

  implicit class DateOps(private val date: LocalDate) extends AnyVal {
    // week starts from friday
    def niftyWeekNo: Int = (date.getDayOfWeek.getValue + date.getDayOfMonth + Calendar.FRIDAY) / 7
  }

}
