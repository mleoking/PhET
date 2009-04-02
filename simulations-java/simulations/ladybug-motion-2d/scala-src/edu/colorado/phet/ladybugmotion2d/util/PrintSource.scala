package edu.colorado.phet.ladybugmotion2d

import scala.io.Source
import java.io.{File, BufferedWriter, FileOutputStream, FileWriter}

object PrintSource {
  def main(args: Array[String]) = {
    val template = new File("C:/Users/Owner/Desktop/SyntaxHighlighter_1.5.1/template.html")
    val templateSource: String = Source.fromFile(template).getLines.mkString

    val file = new File("C:/workingcopy/phet/svn/trunk/simulations-java/simulations/moving-man/scala-src/edu/colorado/phet/movingman/ladybug");
    val children = file.listFiles();

    children.foreach((f: File) => {
      if (f.isFile) {
        val s = Source.fromFile(f).getLines.mkString

        val out = templateSource.replaceAll("@SOURCE@", s)
        println("output\n" + out + "\n");
        val bu = new BufferedWriter(new FileWriter(new File(template.getParentFile, f.getName + ".html")))
        bu.write(out)
        bu.close();

        Nil
      }
    })

    Nil
  }
}