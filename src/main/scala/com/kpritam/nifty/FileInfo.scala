package com.kpritam.nifty

import java.nio.file.Path
import java.util.{Calendar, Date}

import com.kpritam.nifty.Csv.{extractDateFromPath, isCsv}
import com.kpritam.nifty.EitherExt.ListEitherOps

case class NiftyDate(date: Date) {
  private val calendar = Calendar.getInstance()

  def isFriday: Boolean = dayOfWeek == Calendar.FRIDAY

  def isThursday: Boolean = dayOfWeek == Calendar.THURSDAY

  def dayOfWeek: Int = {
    calendar.setTime(date)
    calendar.get(Calendar.DAY_OF_WEEK)
  }

  def dayOfMonth: Int = {
    calendar.setTime(date)
    calendar.get(Calendar.DAY_OF_MONTH)
  }

  def weekNoStartingFromFriday: Int = (dayOfWeek + dayOfMonth + 6) / 7
}

case class FileInfo(path: Path, niftyDate: NiftyDate, weekNo: Int)

object FileInfo {
  def from(path: Path): Either[String, FileInfo] =
    extractDateFromPath(path).map { d =>
      val niftyDate = NiftyDate(d)
      FileInfo(path, niftyDate, niftyDate.weekNoStartingFromFriday)
    }

  def walkCsv(folder: Path): Either[List[String], List[FileInfo]] =
    FileUtils
      .walk(folder)
      .filter(isCsv)
      .map(FileInfo.from)
      .sequence
      .map(_.sortBy(_.niftyDate.date))

  def grpByWeek(files: List[FileInfo]): Set[FileInfosByWeek] =
    files.groupBy(_.weekNo).view.toSet.map((x: (Int, List[FileInfo])) => FileInfosByWeek(x._1, x._2))

  def listAllFilesGroupedByWeek(folder: Path): Either[List[String], Set[FileInfosByWeek]] = walkCsv(folder).map(grpByWeek)

  def startAndEndDayOfWeek(weeks: Set[FileInfosByWeek]): List[(Int, List[FileInfo])] =
    weeks.map {
      case FileInfosByWeek(i, value) => (i, value.headOption.toList ++ value.tail.lastOption.toList)
    }.toList
}

case class FileInfosByWeek private (weekNo: Int, fileInfos: List[FileInfo]) {
  def start: Option[FileInfo]     = fileInfos.headOption
  def end: Option[FileInfo]       = fileInfos.lastOption
  def startAndEnd: List[FileInfo] = start.toList ++ end.toList
}
object FileInfosByWeek {
  def apply(weekNo: Int, fileInfos: List[FileInfo]): FileInfosByWeek =
    new FileInfosByWeek(weekNo, fileInfos.sortBy(_.niftyDate.date))
}
