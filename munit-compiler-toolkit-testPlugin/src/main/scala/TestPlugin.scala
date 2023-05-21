package com
package xebia
package functional
package munitCompilerToolkit

import dotty.tools.dotc.ast.tpd.*
import dotty.tools.dotc.core.Constants.Constant
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Names
import dotty.tools.dotc.core.StdNames
import dotty.tools.dotc.core.Symbols.*
import dotty.tools.dotc.plugins.PluginPhase
import dotty.tools.dotc.plugins.StandardPlugin
import dotty.tools.dotc.transform.Staging

class TestPlugin extends StandardPlugin:
  val name: String = "TestPlugin"
  override val description: String = "Test Plugin"
  override def init(options: List[String]): List[PluginPhase] =
    TestPhase() :: Nil
end TestPlugin

class TestPhase() extends PluginPhase:
  override def phaseName: String = TestPhase.name
  override val runsAfter = Set(Staging.name)
  override def runsBefore: Set[String] = Set("pickleQuotes")
  override def transformApply(tree: Apply)(using Context): Tree =
    if (tree.fun.symbol.name.show != StdNames.nme.CONSTRUCTOR.show)
      val interceptedArgs: List[Tree] =
        List(Literal(Constant(tree.fun.symbol.showFullName))) ::: tree.args
      val newTree =
        if (interceptedArgs.size > 1)
          New(
            requiredClassRef(
              "com.xebia.functional.munitCompilerToolkit.LoggingInterceptor"
            )
          ).select(StdNames.nme.CONSTRUCTOR)
            .appliedToType(tree.fun.symbol.info.finalResultType)
            .appliedTo(tree)
            .select(Names.termName("apply"), _.paramSymss.flatten.size > 1)
            .appliedToArgs(interceptedArgs)
        else
          New(
            requiredClassRef(
              "com.xebia.functional.munitCompilerToolkit.LoggingInterceptor"
            )
          ).select(StdNames.nme.CONSTRUCTOR)
            .appliedToType(tree.fun.symbol.info.finalResultType)
            .appliedTo(tree)
            .select(Names.termName("apply"), _.paramSymss.flatten.size == 1)
            .appliedTo(Literal(Constant(tree.fun.symbol.showFullName)))
      newTree
    else tree
end TestPhase

object TestPhase:
  val name = "testkittestphase"
end TestPhase
