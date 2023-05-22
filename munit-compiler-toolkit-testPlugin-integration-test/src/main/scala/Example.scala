package com
package xebia
package functional
package munitCompilerToolkit


def repeat_prime(s: String, i: Int): String =
  val stringBuilder = java.lang.StringBuilder()
  var x = 0
  while(x < i)
    stringBuilder.append(s)
    x = x + 1
  stringBuilder.toString()

@main def main():Unit =
  println(repeat_prime("Na ", 8)  + "Batmaan!")
