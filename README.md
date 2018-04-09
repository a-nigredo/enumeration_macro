# Enumeration macro
Macro generates case-object-like enumeration. Can be applied to either abstract class with 1 String parameter or trait. Keep in mind that macro updates or creates companion object
### Examples
##### Class application
```
@Enumeration(Seq("Test" -> "testValue"))
abstract class Foo(val value: String)

object Foo {
  object Test extends Foo("testValue")
  val values: Set[Foo] = Set(Test)
  implicit val showFoo: cats.Show[Foo] = (t: Foo) => t.value
  implicit val readFoo: dev.nigredo.Read[Foo] = (t: String) => values.find(_.value == t)
}
```
##### Trait application
```
@Enumeration(Seq("Test" -> "testValue"))
sealed trait Foo

object Foo {
  object Test extends Foo
  val values: Set[Foo] = Set(Test)
  implicit val showFoo: cats.Show[Foo] = (t: Foo) => t match {
    case `Test` => "testValue"
  }
  implicit val readFoo: dev.nigredo.Read[Foo] = (t: String) => t match {
      case "testValue" => Some(Test)
      case _ => None
  }
}
```