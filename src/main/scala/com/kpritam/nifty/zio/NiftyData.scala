package com.kpritam.nifty.zio

import java.time.DayOfWeek

import zio._

import scala.annotation.tailrec

case class NiftyData(csvFiles: List[CsvFile]) {

  def partitionBy(from: DayOfWeek, to: DayOfWeek): IO[String, List[Slot]] = {
    val daysWithinSlot = to.minus(from.getValue).getValue + 1

    @tailrec
    def loop(prev: CsvFile, rem: List[CsvFile], acc: List[Slot]): List[Slot] = {
      val nextDate = prev.date.plusDays(daysWithinSlot)
      rem match {
        case ::(_, _) =>
          val (current, newRem) = rem.partition(_.date.isBefore(nextDate))
          current.partition(_.date.getDayOfWeek != to) match {
            case (_, Nil)            => loop(current.last, newRem, Slot(prev :: current) :: acc)
            case (nf, found :: rest) => loop(found, rest ++ newRem, Slot((prev :: nf) :+ found) :: acc)
          }
        case Nil => acc
      }
    }

    findFirst(from, csvFiles)
      .map { case (first, rest) => loop(first, rest, List.empty).reverse }
  }

  private def findFirst(day: DayOfWeek, csvFiles: List[CsvFile]) =
    csvFiles.dropWhile(_.date.getDayOfWeek != day) match {
      case first :: rest => IO.succeed((first, rest))
      case Nil           => IO.fail(s"File for $day not found in ${csvFiles.mkString("\n")}")
    }

}

object NiftyData {
  def from(folder: String): Task[NiftyData] =
    for {
      paths    <- FileUtils.walkCsv(folder)
      csvFiles <- ZIO.foreachPar(paths)(CsvFile.from).map(_.sorted)
    } yield NiftyData(csvFiles)
}
