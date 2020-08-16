package com.kpritam.nifty.zio

import zio.{Task, ZIO}

case class NiftyData(csvFiles: List[CsvFile])

object NiftyData {
  def from(folder: String): Task[NiftyData] =
    for {
      paths    <- FileUtils.walkCsv(folder)
      csvFiles <- ZIO.foreachPar(paths)(CsvFile.from).map(_.sorted)
    } yield NiftyData(csvFiles)
}
