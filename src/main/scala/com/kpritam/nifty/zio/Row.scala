package com.kpritam.nifty.zio

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import zio.{IO, Task, UIO, ZIO}

case class Row(
    ticker: String,
    date: LocalDate,
    time: String,
    open: Double,
    high: Double,
    low: Double,
    close: Double,
    volume: Double,
    openInterest: Double
)

object Row {
  import kantan.csv._
  import kantan.csv.java8._
  import kantan.csv.generic._
  import kantan.csv.ops._

  private val format                               = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  implicit val dateDecoder: CellDecoder[LocalDate] = localDateDecoder(format)

  private def closeReader[T](reader: CsvReader[T]) = UIO(reader.close())

  def rowReader(csvRawData: String): Task[List[Row]] =
    Task.effect(csvRawData.asCsvReader[Row](rfc.withHeader)).bracket(closeReader) { reader =>
      Task.foreach(reader.iterator.toList)(IO.fromEither(_))
    }

}
