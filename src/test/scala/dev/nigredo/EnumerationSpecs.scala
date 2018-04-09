package dev.nigredo

import cats.Show
import org.specs2.mutable.Specification


class EnumerationSpecs extends Specification {

  @Enumeration(Seq(
    "Test" -> "testValue",
    "Test2" -> "test2Value"
  ))
  abstract class Foo(val value: String)

  @Enumeration(Seq(
    "Test" -> "testValue",
    "Test2" -> "test2Value"
  ))
  abstract class FooWithCompanion(val value: String)

  object FooWithCompanion {

  }

  @Enumeration(Seq(
    "Test" -> "testValue",
    "Test2" -> "test2Value"
  ))
  sealed trait Bar

  @Enumeration(Seq(
    "Test" -> "testValue",
    "Test2" -> "test2Value"
  ))
  sealed trait BarWithCompanion

  object BarWithCompanion {

  }

  "Macro" should {
    "generate values" in {
      "for class without companion object" in {
        Foo.values === Set(Foo.Test, Foo.Test2)
      }
      "for class with companion object" in {
        FooWithCompanion.values === Set(FooWithCompanion.Test, FooWithCompanion.Test2)
      }
      "for trait without companion object" in {
        Bar.values === Set(Bar.Test, Bar.Test2)
      }
      "for trait with companion object" in {
        BarWithCompanion.values === Set(BarWithCompanion.Test, BarWithCompanion.Test2)
      }
    }
    "generate show" in {
      "for class without companion object" in {
        implicitly[Show[Foo]].show(Foo.Test) === "testValue"
        implicitly[Show[Foo]].show(Foo.Test2) === "test2Value"
      }
      "for class with companion object" in {
        implicitly[Show[FooWithCompanion]].show(FooWithCompanion.Test) === "testValue"
        implicitly[Show[FooWithCompanion]].show(FooWithCompanion.Test2) === "test2Value"
      }
      "for trait without companion object" in {
        implicitly[Show[Bar]].show(Bar.Test) === "testValue"
        implicitly[Show[Bar]].show(Bar.Test2) === "test2Value"
      }
      "for trait with companion object" in {
        implicitly[Show[BarWithCompanion]].show(BarWithCompanion.Test) === "testValue"
        implicitly[Show[BarWithCompanion]].show(BarWithCompanion.Test2) === "test2Value"
      }
    }
    "generate read" in {
      "for class without companion object" in {
        implicitly[Read[Foo]].read("testValue") === Some(Foo.Test)
        implicitly[Read[Foo]].read("test2Value") === Some(Foo.Test2)
        implicitly[Read[Foo]].read("notExists") === None
      }
      "for class with companion object" in {
        implicitly[Read[FooWithCompanion]].read("testValue") === Some(FooWithCompanion.Test)
        implicitly[Read[FooWithCompanion]].read("test2Value") === Some(FooWithCompanion.Test2)
        implicitly[Read[FooWithCompanion]].read("notExists") === None
      }
      "for trait without companion object" in {
        implicitly[Read[Bar]].read("testValue") === Some(Bar.Test)
        implicitly[Read[Bar]].read("test2Value") === Some(Bar.Test2)
        implicitly[Read[Bar]].read("notExists") === None
      }
      "for trait with companion object" in {
        implicitly[Read[BarWithCompanion]].read("testValue") === Some(BarWithCompanion.Test)
        implicitly[Read[BarWithCompanion]].read("test2Value") === Some(BarWithCompanion.Test2)
        implicitly[Read[BarWithCompanion]].read("notExists") === None
      }
    }
  }
}
