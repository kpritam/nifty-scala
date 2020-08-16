package com.kpritam.nifty.zio

import java.time.DayOfWeek

import zio._

object ZioApp extends App {
  private val input = "/Users/pritamkadam/Downloads/Nifty final"

  private def readAllCsv(folder: String) =
    for {
      data  <- NiftyData.from(folder)
      slots <- data.partitionBy(DayOfWeek.THURSDAY, DayOfWeek.THURSDAY)
      _     <- ZIO.foreach(slots)(_.prettyPrint)
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = readAllCsv(input).exitCode
}
