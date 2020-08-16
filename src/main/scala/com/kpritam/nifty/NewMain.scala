package com.kpritam.nifty

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import java.time.DayOfWeek

import com.kpritam.nifty.domain.{InputRow, NiftyFile}
import com.kpritam.nifty.domain.NiftyFile.FormatErrors
import com.kpritam.nifty.syntax.EitherExt.ListEitherOps
import com.kpritam.nifty.utils.Csv

import scala.annotation.tailrec

object NewMain extends App {
  private val folder = Paths.get("/Users/pritamkadam/Downloads/Nifty final")
  private val output = "output.csv"

  def write(content: String, output: String) = Files.write(Paths.get(output), content.getBytes(StandardCharsets.UTF_8))

  Nifty
    .readRows(folder, DayOfWeek.THURSDAY, DayOfWeek.THURSDAY, startTime = "9:30", endTime = "14:30")
    .map(Nifty.toCsv)
    .map(write(_, output))
    .left
    .map(e => println(e.mkString("\n")))

}

object Nifty {

  def toCsv(row1: InputRow, row2: InputRow, diff: Double) = s"${row1.write(diff)}\n${row2.write(diff)}"
  def toCsv(tuples: List[(InputRow, InputRow, Double)]): String =
    tuples
      .map {
        case (row1, row2, d) =>
          println(s"${row1.date} ${row1.date.getDayOfWeek}  <-->  ${row2.date} ${row2.date.getDayOfWeek} = $d")
          toCsv(row1, row2, d)
      }
      .mkString("\n")

  private def allCsvFiles(folder: Path): Either[FormatErrors, List[NiftyFile]] =
    Csv.walk(folder).map(NiftyFile(_)).sequence.map(_.sorted)

  def readRows(
      folder: Path,
      startDay: DayOfWeek,
      endDay: DayOfWeek,
      startTime: String,
      endTime: String
  ): Either[FormatErrors, List[(InputRow, InputRow, Double)]] =
    allCsvFiles(folder).flatMap(
      findAll(startDay, endDay, _)
        .map {
          case (start, end) => calcClosingPriceDiff(start, end, startTime, endTime)
        }
        .sequence
        .left
        .map(_.flatten)
    )

  private def calcClosingPriceDiff(start: NiftyFile, end: NiftyFile, startTime: String, endTime: String) = {
    def read(niftyFile: NiftyFile, time: String) =
      niftyFile.read.flatMap(_.rows.find(_.time.startsWith(time)).toRight(List(s"${niftyFile.path} does not contain time $time")))

    for {
      row1 <- read(start, startTime)
      row2 <- read(end, endTime)
    } yield (row1, row2, (row2.close.toDouble - row1.close.toDouble))
  }

  def findAll(startDay: DayOfWeek, endDay: DayOfWeek, files: List[NiftyFile]): List[(NiftyFile, NiftyFile)] = {
    val diff = endDay.minus(startDay.getValue).getValue + 1

    @tailrec
    def loop(start: NiftyFile, rem: List[NiftyFile], result: List[(NiftyFile, NiftyFile)]): List[(NiftyFile, NiftyFile)] =
      rem match {
        case ::(_, _) =>
          val nextDate        = start.date.plusDays(diff)
          val (curr, rest)    = rem.partition(_.date.isBefore(nextDate))
          val (end, leftOver) = findOrLast(endDay, curr)
          val newRem          = leftOver ++ rest

          val newResult = (start, end) :: result
          if (diff < 8) {
            val endOpt = newRem.find(nf => nf.date.getDayOfWeek == startDay || nf.date.isAfter(nextDate))
            endOpt match {
              case Some(end) => loop(end, newRem, newResult)
              case None      => newResult
            }
          }
          else loop(end, newRem, newResult)

        case Nil => result
      }

    val (first, rem) = findFirst(startDay, files)
    loop(first, rem, List.empty)
  }.reverse

  private def findFirst(day: DayOfWeek, files: List[NiftyFile]) = {
    val i = files.indexWhere(_.date.getDayOfWeek == day)
    (files(i), files.drop(i))
  }

  private def findOrLast(day: DayOfWeek, slot: List[NiftyFile]) =
    slot
      .find(_.date.getDayOfWeek == day)
      .fold((slot.last, List.empty[NiftyFile]))((_, slot.dropWhile(_.date.getDayOfWeek != day).drop(1)))

}
