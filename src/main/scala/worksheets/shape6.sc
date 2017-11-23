object ListTest {
  sealed trait MyList {
    def ::(a: Any): MyList = ListTest.::(a, this)
  }

  case class ::(head: Any, tail: MyList) extends MyList

  case object MyNil extends MyList

  type MyNil = Nil.type
}

import ListTest._

val ml: MyList = 1 :: "abc" :: MyNil