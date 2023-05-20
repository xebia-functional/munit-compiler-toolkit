package com
package xebia
package functional
package munitCompilerToolkit

import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.plugins.StandardPlugin
import dotty.tools.dotc.ast.tpd.*
import dotty.tools.dotc.core.Names
import dotty.tools.dotc.core.Symbols.*
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.ast.TreeTypeMap
import dotty.tools.dotc.core.Constants.Constant

class TestPlugin extends StandardPlugin:
  val name: String = "TestPlugin"
  override val description: String = "Test Plugin"
  def init(options: List[String]): List[PluginPhase] = TestPhase() :: Nil
end TestPlugin

class TestPhase() extends PluginPhase:
  override def phaseName: String = TestPhase.name
  override def transformApply(tree: Apply)(using Context): Tree =
    TreeTypeMap(treeMap = {
      case a @ Apply(fn, args)
          if !fn.symbol.info.hasClassSymbol(
            requiredClass(
              "com.xebia.functional.munitCompilerToolkit.LoggingInterceptor"
            )
          ) =>
        New(
          requiredClassRef(
            "com.xebia.functional.munitCompilerToolkit.LoggingInterceptor"
          ),
          List(a)
        )
          .select(Names.termName("apply"))
          .appliedTo(
            Literal(Constant(fn.symbol.showFullName)),
            ref(requiredModule("scala.collection.immutable.List"))
              .appliedToType(summon.definitions.AnyType)
              .appliedToArgs(args)
          )
      case t => t
    })(tree)
end TestPhase

object TestPhase:
  val name = "testkittestphase"
end TestPhase
