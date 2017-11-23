object ListTest {
  sealed trait MyList[+A] {
    def ::[B >: A](a: B): MyList[B] = ListTest.::(a, this)
  }

  case class ::[A](head: A, tail: MyList[A]) extends MyList[A]

  case object MyNil extends MyList[Nothing]

  type MyNil = Nil.type
}

import ListTest._

val ml: MyList[Int] = 1 :: 2 :: MyNil

