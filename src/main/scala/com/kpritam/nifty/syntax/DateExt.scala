package com.kpritam.nifty.syntax

import java.util.{Calendar, Date}

object DateExt {
  private val calendar = Calendar.getInstance()

  implicit class DateOps(private val date: Date) extends AnyVal {
    // week starts from friday
    def niftyWeekNo: Int = (dayOfWeek + dayOfMonth + Calendar.FRIDAY) / 7

    def dayOfWeek: Int  = dayOf(date, Calendar.DAY_OF_WEEK)
    def dayOfMonth: Int = dayOf(date, Calendar.DAY_OF_MONTH)

    private def dayOf(date: Date, n: Int): Int = {
      calendar.setTime(date)
      calendar.get(n)
    }
  }

}
