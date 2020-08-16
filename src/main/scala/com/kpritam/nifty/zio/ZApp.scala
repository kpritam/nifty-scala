package com.kpritam.nifty.zio

import java.io.File
import java.time.DayOfWeek

import zio._
import zio.console.{Console, putStrLn}

object ZApp extends App {
  private val input      = "/Users/pritamkadam/Downloads/Nifty final"
  private val outputFile = new File("output1.csv")

  private def readAllCsv(folder: String) =
    for {
      data   <- NiftyData.from(folder)
      slots  <- data.partitionBy(DayOfWeek.THURSDAY, DayOfWeek.THURSDAY)
      output <- ZIO.foreach(slots)(_.calculateClosingPriceDiff(startTime = "9:30", endTime = "14:30"))
      _      <- OutputRow.writeCsv(outputFile, output.flatMap(t => List(t._1, t._2)))
      _      <- Logger.log(output)
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = readAllCsv(input).exitCode
}

object Logger {
  def log(output: List[(OutputRow, OutputRow)]): URIO[Console, List[Unit]] =
    ZIO.foreach(output.map {
      case (f, s) => s"${f.date} ${f.date.getDayOfWeek} <--> ${s.date} ${s.date.getDayOfWeek} = ${f.diff}"
    })(putStrLn(_))
}
