package com.kpritam.nifty

object EitherExt {

  implicit class ListEitherOps[L, R](private val le: List[Either[L, R]]) extends AnyVal {

    def sequence: Either[List[L], List[R]] =
      le.partition(_.isLeft) match {
        case (Nil, rs) => Right(for (Right(i) <- rs) yield i)
        case (ls, _)   => Left(for (Left(s) <- ls) yield s)
      }

  }

}
