package com.kpritam.nifty

import java.nio.file.Paths

import com.kpritam.nifty.domain.Weeks

object Main extends App {
  private val folder   = Paths.get("/Users/pritamkadam/Downloads/APR-2020")
  private val allWeeks = Weeks.from(folder).flatMap(_.calculateStartEndPriceDiff(startTime = "9:30", endTime = "14:30"))
  private val result   = allWeeks.left.map(_.mkString("\n")).map(_.mkString("\n"))

  println(result)
}
