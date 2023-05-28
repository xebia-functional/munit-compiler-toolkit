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

import scala.util.Try

class LoggingInterceptor[A](intercepted: => A):
  def apply(name: String): A = 
    println(s"[Interceptor]: called: $name")
    val result = Try(intercepted)
    println(s"[Interceptor]: result is ${result}")
    result.get
  def apply(name: String)(arguments: Any*): A = 
    println(s"[Interceptor]: called: $name with ${arguments.map(_.toString()).mkString(",")}")
    val result = Try(intercepted)
    println(s"[Interceptor]: result is ${result}")
    result.get