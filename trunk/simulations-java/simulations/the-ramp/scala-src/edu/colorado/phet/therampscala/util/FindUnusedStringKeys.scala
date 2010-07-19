package edu.colorado.phet.therampscala.util

import collection.mutable.ArrayBuffer
import io.Source
import java.io.{File, FileInputStream}
import java.util.Properties

object FindUnusedStringKeys {
  def main(args: Array[String]) {
    val properties = new Properties
    properties.load(new FileInputStream("C:/workingcopy/phet/svn/trunk/simulations-java/simulations/the-ramp/data/the-ramp/localization/the-ramp-strings.properties"))

    val keySet = properties.keys
    while (keySet.hasMoreElements) {
      var key = keySet.nextElement()
      val usages = findUsagesInDir(key.toString, new File("C:/workingcopy/phet/svn/trunk/simulations-java/simulations/the-ramp/scala-src"))
      if (usages.length == 0) {
        println("unused key: " + key)
      }
      else {
        println("used key: " + key + ": usages = " + usages.mkString(", "))
      }
    }
  }
  case class Result(line: String, file: File)
  def findUsagesInDir(key: String, file: File): Seq[Result] = {
    val results = new ArrayBuffer[Result]
    for (f <- file.listFiles) {
      if (f.isDirectory) results ++= findUsagesInDir(key, f)
      else results ++= findUsagesInFile(key, f)
    }
    results
  }

  def findUsagesInFile(key: String, file: File) = {
    val results = new ArrayBuffer[Result]
    val lines = Source.fromFile(file).getLines
    for (line <- lines) {
      if (line.contains(key)) results += new Result(line, file)
    }
    results
  }
}