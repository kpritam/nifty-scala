package com.kpritam.nifty

import java.nio.file.{Path, Paths}
import java.time.DayOfWeek

import com.kpritam.nifty.domain.NiftyFile
import com.kpritam.nifty.syntax.EitherExt.ListEitherOps
import com.kpritam.nifty.utils.Csv

import scala.annotation.tailrec

object NewMain extends App {
  private val folder = Paths.get("/Users/pritamkadam/Downloads/Nifty final")

  Nifty
    .calculate(folder, DayOfWeek.THURSDAY, DayOfWeek.THURSDAY, startTime = "9:30", endTime = "14:30")
    .foreach(println)

}

object Nifty {

  def calculate(folder: Path, startDay: DayOfWeek, endDay: DayOfWeek, startTime: String, endTime: String): List[String] = {
    val allFiles: List[NiftyFile] =
      Csv
        .walk(folder)
        .map(NiftyFile(_))
        .sequence
        .map(_.sorted)
        .fold(e => throw new RuntimeException(e.mkString(",")), identity)

    println("======================== INPUT ========================")
    allFiles.foreach(nf => println(s"${nf.date} ${nf.date.getDayOfWeek}"))
    println("=" * 80)

    findAll(startDay, endDay, allFiles)
      .map {
        case (start, end) =>
          def read(niftyFile: NiftyFile, time: String) = {
            val rows = niftyFile.read
              .fold(e => throw new RuntimeException(e.mkString(",")), identity)
              .rows
            rows
              .find(_.time.startsWith(time))
              .map(_.close)
              .getOrElse {
                println(s"Invalid file: ${niftyFile.path}")
                "0"
              }
          }

          val s = read(start, startTime)
          val e = read(end, endTime)

          val d = e.toDouble - s.toDouble
          s"[${start.date} ${start.date.getDayOfWeek}  <-->  ${end.date} ${end.date.getDayOfWeek} = $d]"
      }
  }

  def findAll(startDay: DayOfWeek, endDay: DayOfWeek, files: List[NiftyFile]): List[(NiftyFile, NiftyFile)] = {
    val diff = endDay.minus(startDay.getValue).getValue + 1

    @tailrec
    def loop(prev: NiftyFile, rem: List[NiftyFile], result: List[(NiftyFile, NiftyFile)]): List[(NiftyFile, NiftyFile)] =
      rem match {
        case ::(_, _) =>
          val nextDate       = prev.date.plusDays(diff)
          val (curr, newRem) = rem.partition(_.date.isBefore(nextDate))
          val (last, newRem1) =
            curr
              .find(_.date.getDayOfWeek == endDay)
              .fold((curr.last, newRem)) { f =>
                (f, curr.dropWhile(_.date.getDayOfWeek != endDay).drop(1) ++ newRem)
              }

          val newResult = (prev, last) :: result
          if (diff < 8) {
            val f = newRem1.find(nf => nf.date.getDayOfWeek == startDay || nf.date.isAfter(nextDate))
            f match {
              case Some(value) => loop(value, newRem1, newResult)
              case None        => newResult
            }
          }
          else loop(last, newRem1, newResult)

        case Nil => result
      }

    val i         = files.indexWhere(_.date.getDayOfWeek == startDay)
    val firstFile = files(i)
    val rem       = files.drop(i)
    loop(firstFile, rem, List.empty)
  }.reverse

}
