/*
 * Copyright 2023 Xebia Functional
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
  private def extractNestedArgs(
      tree: Tree,
      args: List[List[Tree]]
  ): List[List[Tree]] =
    tree match {
      case Apply(b @ Apply(fn, _), largs) =>
        extractNestedArgs(b, args.appended(largs))
      case Apply(b @ TypeApply(fn, _), largs) =>
        extractNestedArgs(b, args.appended(largs))
      case Apply(fn, largs) => args.appended(largs)
      case _                => args
    }
  override def transformApply(tree: Apply)(using Context): Tree =
    if (
      tree.fun.symbol.name.show == StdNames.nme.CONSTRUCTOR.show && tree.fun.symbol.name.show != "<none>" && !tree.fun.symbol.annotations
        .exists(_.symbol.name.show != "main")
    ) tree
    else
      val extractedArgs = extractNestedArgs(tree, Nil).filterNot(_.isEmpty)
      val argsToLog = (List(
        Literal(Constant(tree.fun.symbol.showFullName))
      ) :: extractedArgs).flatten
      if (extractedArgs.isEmpty)
        New(
          requiredClassRef(
            "com.xebia.functional.munitCompilerToolkit.LoggingInterceptor"
          )
        ).select(StdNames.nme.CONSTRUCTOR)
          .appliedToType(tree.fun.symbol.info.finalResultType)
          .appliedTo(tree)
          .select(Names.termName("apply"), _.paramSymss.flatten.size == 1)
          .appliedToArgs(argsToLog)
      else
        New(
          requiredClassRef(
            "com.xebia.functional.munitCompilerToolkit.LoggingInterceptor"
          )
        ).select(StdNames.nme.CONSTRUCTOR)
          .appliedToType(tree.fun.symbol.info.finalResultType)
          .appliedTo(tree)
          .select(Names.termName("apply"), _.paramSymss.flatten.size > 1)
          .appliedToArgs(
            List(
              Literal(Constant(tree.fun.symbol.showFullName))
            )
          )
          .appliedToVarargs(
            extractedArgs.flatten,
            TypeTree(summon[Context].definitions.AnyType)
          )
end TestPhase

object TestPhase:
  val name = "testkittestphase"
end TestPhase
