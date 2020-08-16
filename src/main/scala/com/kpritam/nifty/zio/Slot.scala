package com.kpritam.nifty.zio

import zio.console.{Console, putStrLn}
import zio.{RIO, Task}

case class Slot(files: List[CsvFile]) {

  def calculateClosingPriceDiff(startTime: String, endTime: String): Task[(OutputRow, OutputRow)] =
    (files.headOption, files.lastOption) match {
      case (Some(start), Some(end)) =>
        for {
          s <- start.rowAt(startTime)
          e <- end.rowAt(endTime)
        } yield {
          val d = e.close - s.close
          (s.toOutput(d), e.toOutput(d))
        }
      case _ => Task.fail(new RuntimeException(msg))
    }

  def startAndEnd: Option[String] =
    (files.headOption zip files.lastOption).map {
      case (start, end) => s"${start.date} ${start.date.getDayOfWeek} <--> ${end.date} ${end.date.getDayOfWeek}"
    }

  def msg: String = files.map(f => (f.date, f.date.getDayOfWeek)).mkString("\n")

  def prettyPrint: RIO[Console, Unit] = putStrLn("======= SLOT =======") *> putStrLn(msg)
}
