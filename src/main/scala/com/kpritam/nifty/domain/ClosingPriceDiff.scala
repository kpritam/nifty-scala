package com.kpritam.nifty.domain

case class ClosingPriceDiff(startPrice: Double, endPrice: Double) {
  val diff: Double  = endPrice - startPrice
  def print: String = s"[Start Price: $startPrice, End Price: $endPrice, Diff: $diff]"

  override def toString: String = print
}
