object ListTest {
  sealed trait MyList {
    def ::[A](a: A): A :: this.type = ListTest.::(a, this)
  }

  case class ::[H, T <: MyList](head: H, tail: T) extends MyList

  case object MyNil extends MyList

  type MyNil = MyNil.type
}

import ListTest._

val ml: Int :: String :: MyNil = 1 :: "abc" :: MyNil