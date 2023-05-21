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
         |    val x: String = new com.xebia.functional.munitCompilerToolkit.LoggingInterceptor[String](example("test")).apply("example", "test")
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
end MunitCompilerToolkitTestkitSuite
