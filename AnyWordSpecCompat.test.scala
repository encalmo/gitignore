package org.encalmo.utils

import scala.reflect.ClassTag

trait AnyWordSpecCompat extends munit.FunSuite {

  extension [T](name: String) {

    def in(body: => T): Unit = test(name)(body)

    def should(body: => T): Unit = {
      val pos0 = munitTestsBuffer.length
      body
      val pos1 = munitTestsBuffer.length
      for (i <- pos0 until pos1)
        munitTestsBuffer(i) = {
          val test = munitTestsBuffer(i)
          test.withName(name + " should " + test.name)
        }
    }
  }

  implicit class IterableExt[T](val iterable: Iterable[T]) {

    def shouldBe(expected: Iterable[T])(implicit loc: munit.Location): Unit = {
      assert(
        iterable.size == expected.size,
        s"both collections must have the same size,\n expected ${expected.size}: ${expected.toSeq}, but\n received ${iterable.size}: ${iterable.toSeq}"
      )
      iterable.zip(expected).foreach { case (a, b) =>
        if (a.isInstanceOf[Iterable[?]] && b.isInstanceOf[Iterable[?]])
          a.asInstanceOf[Iterable[T]].shouldBe(b.asInstanceOf[Iterable[T]])
        else assertEquals(a, b)
      }
    }

    def ===(expected: Iterable[T])(implicit loc: munit.Location): Unit =
      shouldBe(expected)

  }

  extension [T](array: Array[T]) {

    def shouldBe(expected: Iterable[T])(implicit loc: munit.Location): Unit = {
      assert(
        array.size == expected.size,
        s"both collections must have the same size, expected ${expected.mkString("[", ",", "]")}, but got ${array
            .mkString("[", ",", "]")}"
      )
      array.zip(expected).zipWithIndex.foreach { case ((a, b), i) =>
        if (a.isInstanceOf[Array[Int]] && b.isInstanceOf[Array[Int]]) {
          a.asInstanceOf[Array[Int]].shouldBe(b.asInstanceOf[Array[Int]])
        } else assertEquals(a, b, s"Values at index $i are not same")
      }
    }

    def ===(expected: Iterable[T])(implicit loc: munit.Location): Unit =
      shouldBe(expected)
  }

  extension [T](value: T) {

    def shouldBe(expected: T)(implicit loc: munit.Location): Unit =
      assertEquals(value, expected)

    def should(word: not.type): NotWord[T] = NotWord(value)

    def ===(expected: T)(implicit loc: munit.Location): Unit =
      shouldBe(expected)

    def !==(unexpected: T)(implicit loc: munit.Location): Unit =
      assertNotEquals(value, unexpected)
  }

  case object not
  case object thrownBy {
    def apply[T](body: => T): ThrownByWord[T] = ThrownByWord(() => body)
  }

  case class NotWord[T](value: T) {
    def be(expected: T)(implicit loc: munit.Location): Unit =
      assertNotEquals(value, expected)
  }

  def a[E <: Throwable: ClassTag]: AnWord[E] = new AnWord[E]
  def an[E <: Throwable: ClassTag]: AnWord[E] = new AnWord[E]

  class AnWord[E <: Throwable: ClassTag] {
    def shouldBe[T](thrownBy: ThrownByWord[T]): Unit =
      intercept[E](thrownBy.body())
  }

  case class ThrownByWord[T](body: () => T)

}
