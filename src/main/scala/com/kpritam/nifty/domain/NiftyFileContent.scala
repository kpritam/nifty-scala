package com.kpritam.nifty.domain

case class NiftyFileContent(rows: List[Row]) {
  def closePriceAt(time: String): Option[Double] = rows.find(_.time.startsWith(time)).map(_.close.toDouble)
}
