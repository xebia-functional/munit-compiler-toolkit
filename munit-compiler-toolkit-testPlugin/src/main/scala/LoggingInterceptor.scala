package com
package xebia
package functional
package munitCompilerToolkit

import scala.util.Try

class LoggingInterceptor[A](intercepted: => A):
  def apply(name: String, arguments: List[Any]): A = 
    println(s"[Interceptor]: called: $name with ${arguments.map(_.toString).mkString(",")}")
    val result = Try(intercepted)
    println(s"[Interceptor]: result is ${result}")
    result.get
