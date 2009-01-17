package edu.colorado.phet.movingman.ladybug

import scala.io.Source
import java.io.File

object TestSetter {
  def main(args: Array[String]) = {

    class Time {
      var h = 12

      def hour: Int = h

      def hour_=(x: Int) = {h = x}
    }
    val t = new Time
    t.hour = 123

    val file = new File("C:/workingcopy/phet/svn/trunk/simulations-java/simulations/moving-man/scala-src/edu/colorado/phet/movingman/ladybug");
    val children = file.listFiles();

    var count = 0;
    children.foreach((f: File) => {
      if (f.isFile) {
        val s: Source = Source.fromFile(f)
        val c = s.getLines.toList.length
        println(f.getAbsolutePath + ": " + c)
        count = count + c
        //        print("running count="+count)
      }
    })
    println("Total count: " + count);
  }
}