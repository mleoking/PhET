package edu.colorado.phet.movingman.ladybug.util

import java.text.SimpleDateFormat
import java.util.Date

object TestDateFormat {
  def main(args: Array[String]) {
    println(new SimpleDateFormat("MMM d, yyyy").format(new Date(System.currentTimeMillis + 1000 * 60)))
    for (i <- 0 to 100000)
      println(new SimpleDateFormat("MMM d, yyyy").format(new Date(System.currentTimeMillis + i * 100 * 60 * 60)))
  }
}