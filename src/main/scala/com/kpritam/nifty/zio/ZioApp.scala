package com.kpritam.nifty.zio

import zio._
import console._

object ZioApp extends App {
  private val input = "/Users/pritamkadam/Downloads/APR-2020"

  private def readAllCsv(folder: String) =
    for {
      data <- NiftyData.from(folder)
      _    <- putStrLn(data.csvFiles.map(_.date).mkString("\n"))
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    readAllCsv(input).mapError(_.getMessage).exitCode
}
