object ListTest {
  sealed trait MyList {
    //def ::(a: Any): MyList = ListTest.::(a, this)
  }

  case class ::[H, T <: MyList]() extends MyList

  case object MyNil extends MyList

  type MyNil = Nil.type
}

import ListTest._

type ml = Int :: String :: MyNil

