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

def repeat_prime(s: String, i: Int): String =
  val stringBuilder = java.lang.StringBuilder()
  var x = 0
  while (x < i)
    stringBuilder.append(s)
    x = x + 1
  stringBuilder.toString()

@main def main(): Unit =
  println(repeat_prime("Na ", 8) + "Batman!")
