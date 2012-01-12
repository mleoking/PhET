// Copyright 2002-2011, University of Colorado
package org.reid.scenic

import java.io.File

/**
 * @author Sam Reid
 */

object CountLines extends App {
  //  val dir = new File("C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\force-law-lab\\scala-src")
  val dir = new File("C:\\workingcopy\\phet\\svn\\trunk\\simulations-flex\\simulations\\resonance\\src\\edu\\colorado\\phet\\resonance")
  val lines = count(dir)
  println("lines = " + lines)

  def count(f: File): Int = {
    if ( f.isDirectory && f.getName != ".svn" ) {( for ( d: File <- f.listFiles() ) yield {count(d)} ).foldLeft(0)((a, b) => a + b)}
    else if ( f.getName.endsWith(".java") || f.getName.endsWith(".scala") || f.getName.endsWith(".as") ) {scala.io.Source.fromFile(f).getLines().length}
    else {0}
  }
}