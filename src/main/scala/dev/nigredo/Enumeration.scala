package dev.nigredo

import scala.reflect.macros.blackbox
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

class Enumeration(values: Seq[(String, String)]) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro EnumerationMacro.impl
}

object EnumerationMacro {

  def impl(c: blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val config = c.prefix.tree.duplicate match {
      case q"new $annot(values = $loggerName)" =>
        c.eval[Seq[(String, String)]](c.Expr(loggerName.asInstanceOf[Tree]))
      case q"new $annot($loggerName)" =>
        c.eval[Seq[(String, String)]](c.Expr(loggerName.asInstanceOf[Tree]))
    }

    annottees.map(_.tree) match {
      case clazz@q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" :: tail =>
        val params = paramss.flatten
        if (params.size != 1) c.abort(c.enclosingPosition, "Enum base class has to have only 1 String parameter")
        else {
          val paramName = params.head match {
            case c: ValDef => c.name
            case _ => c.abort(c.enclosingPosition, "Enum base class parameter has to be Val definition")
          }
          val tName = tpname.asInstanceOf[TypeName].encodedName.toString
          val enumCompanionBody =
            q"""
               ..${config.map(x => q"case object ${TermName(x._1)} extends $tpname(${x._2})")}
              val values: Set[$tpname] = Set(..${config.map(x => TermName(x._1))})
              implicit val ${TermName(s"show$tName")}: cats.Show[$tpname] = (t: $tpname) => t.$paramName
              implicit val ${TermName(s"read$tName")}: dev.nigredo.Read[$tpname] = (t: String) => values.find(_.$paramName == t)
             """
          val companion = tail match {
            case q"object $obj extends ..$bases { ..$body }" :: _ =>
              q"object $obj extends ..$bases {..$body;..$enumCompanionBody}"
            case _ => q"object ${TermName(tpname.toString)}{..$enumCompanionBody}"
          }
          c.Expr[Any](q"${clazz.head};$companion")
        }
      case tr@q"$mods trait $tpname[..$tparams] extends { ..$earlydefns } with ..$parents { $self => ..$stats }" :: tail =>
        val tName = tpname.asInstanceOf[TypeName].encodedName.toString
        val enumCompanionBody =
          q"""
               ..${config.map(x => q"case object ${TermName(x._1)} extends $tpname")}
              val values: Set[$tpname] = Set(..${config.map(x => TermName(x._1))})
              implicit val ${TermName(s"show$tName")}: cats.Show[$tpname] = (t: $tpname) => t match {
                  case ..${config.map(x => cq"`${TermName(x._1)}` => ${x._2}")}
              }
              implicit val ${TermName(s"read$tName")}: dev.nigredo.Read[$tpname] = (t: String) => t match {
                 case ..${config.map(x => cq"${x._2} => Some(${TermName(x._1)})")}
                 case _ => None
              }
             """
        val companion = tail match {
          case q"object $obj extends ..$bases { ..$body }" :: _ =>
            q"object $obj extends ..$bases {..$body;..$enumCompanionBody}"
          case _ => q"object ${TermName(tpname.toString)}{..$enumCompanionBody}"
        }
        c.Expr[Any](q"${tr.head};$companion")
      case _ => c.abort(c.enclosingPosition, "@Enum can be applied to Class only")
    }
  }
}
