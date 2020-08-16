package com.kpritam.nifty.zio

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import zio.stream._
import zio.{Task, UIO}

import scala.io.BufferedSource

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
  import kantan.csv.generic._
  import kantan.csv.java8._
  import kantan.csv.ops._

  private val format                               = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  implicit val dateDecoder: CellDecoder[LocalDate] = localDateDecoder(format)

  private def closeReader[T](reader: CsvReader[T]) = UIO(reader.close())

  def rowReader(source: BufferedSource): Stream[Throwable, Row] =
    Stream
      .bracket(Task.effect(source.reader().asCsvReader[Row](rfc.withHeader)))(closeReader)
      .flatMap(reader => Stream.absolve(Stream.fromIterator(reader.iterator)))

}
