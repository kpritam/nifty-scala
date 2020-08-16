package com.kpritam.nifty.zio

import java.io.File
import java.time.DayOfWeek
import java.util.concurrent.TimeUnit

import zio._
import zio.clock._
import zio.console.{Console, putStrLn}
import zio.duration.durationInt

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

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = measure(readAllCsv(input).exitCode)

  def measure[R, E, A](f: => ZIO[R, E, A]): ZIO[Console with Clock with R, E, A] = {
    for {
      start <- currentTime(TimeUnit.MILLISECONDS)
      a     <- f
      end   <- currentTime(TimeUnit.MILLISECONDS)
      _     <- putStrLn(s"Total time taken = ${(end - start) / 1000}s")
    } yield a

  }
}

object Logger {
  def log(output: List[(OutputRow, OutputRow)]): URIO[Console, List[Unit]] =
    ZIO.foreach(output.map {
      case (f, s) => s"${f.date} ${f.date.getDayOfWeek} <--> ${s.date} ${s.date.getDayOfWeek} = ${f.diff}"
    })(putStrLn(_))
}
