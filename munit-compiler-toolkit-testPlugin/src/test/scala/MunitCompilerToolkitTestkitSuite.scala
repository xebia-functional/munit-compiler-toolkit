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

import dotty.tools.dotc.core.Contexts.Context

class MunitCompilerToolkitTestkitSuite extends CompilerSuite:

  compilerTest("compilerTest should pick up the TestPlugin from the testArgs")(
    """|def example(s: String): String =
       |  s
       |object Thing {
       |  val x = example("test")
       |}
       |""".stripMargin,
    Option("pickleQuotes")
  ) { case (tree, given Context) =>
    assertEquals(
      cleanCompilerOutput(tree),
      """|package <empty> {
         |  final lazy module val Thing: Thing = new Thing()
         |  @SourceFile("compileFromStringscala") final module class Thing() extends Object() { this: Thing.type =>
         |    private def writeReplace(): AnyRef = new scala.runtime.ModuleSerializationProxy(classOf[Thing.type])
         |    val x: String = new com.xebia.functional.munitCompilerToolkit.LoggingInterceptor[String](example("test")).apply("example")(["test" : Any]*)
         |  }
         |  final lazy module val compileFromStringpackage:
         |    compileFromStringpackage
         |   = new compileFromStringpackage()
         |  @SourceFile("compileFromStringscala") final module class
         |    compileFromStringpackage
         |  () extends Object() { this: compileFromStringpackage.type =>
         |    private def writeReplace(): AnyRef =
         |      new scala.runtime.ModuleSerializationProxy(classOf[compileFromStringpackage.type])
         |    def example(s: String): String = s
         |  }
         |}""".stripMargin
    )
  }

  val sourceFixture = FunFixture[String](
    setup = _ => {
      """|def example(s: String): String =
       |  s
       |object Thing {
       |  val x = example("test")
       |}
       |""".stripMargin
    },
    teardown = _ => ()
  )

  val expectedTreeFixture = FunFixture[String](
    setup = _ => {
      """|package <empty> {
         |  final lazy module val Thing: Thing = new Thing()
         |  @SourceFile("compileFromStringscala") final module class Thing() extends Object() { this: Thing.type =>
         |    private def writeReplace(): AnyRef = new scala.runtime.ModuleSerializationProxy(classOf[Thing.type])
         |    val x: String = new com.xebia.functional.munitCompilerToolkit.LoggingInterceptor[String](example("test")).apply("example")(["test" : Any]*)
         |  }
         |  final lazy module val compileFromStringpackage:
         |    compileFromStringpackage
         |   = new compileFromStringpackage()
         |  @SourceFile("compileFromStringscala") final module class
         |    compileFromStringpackage
         |  () extends Object() { this: compileFromStringpackage.type =>
         |    private def writeReplace(): AnyRef =
         |      new scala.runtime.ModuleSerializationProxy(classOf[compileFromStringpackage.type])
         |    def example(s: String): String = s
         |  }
         |}""".stripMargin
    },
    teardown = _ => ()
  )

  val sourceFixtureAndExpectedTreeFixture =
    FunFixture.map2(sourceFixture, expectedTreeFixture)

  sourceFixtureAndExpectedTreeFixture.compilerTest(
    "compilerTest should pick up the TestPlugin from the testArgs"
  )(Option("pickleQuotes")) { case (source, _) =>
    source
  } {
    case (_, expectedTree) => { case (actualTree, given Context) =>
      assertEquals(cleanCompilerOutput(actualTree), expectedTree)
    }
  }

end MunitCompilerToolkitTestkitSuite
