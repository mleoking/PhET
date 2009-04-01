package edu.colorado.phet.movingman.ladybug

import scala.io.Source
import java.io.File

object Metrics {
  def main(args: Array[String]) = {

    val file = new File("C:/workingcopy/phet/svn/trunk/simulations-java/simulations/moving-man/scala-src/edu/colorado/phet/movingman/ladybug");
    val children = file.listFiles();

    var count = 0;
    children.foreach((f: File) => {
      if (f.isFile) {
        val s: Source = Source.fromFile(f)
        val c = s.getLines.toList.length
        println(f.getAbsolutePath + ": " + c)
        count = count + c
      }
    })
    println("Total count: " + count);


  }
}