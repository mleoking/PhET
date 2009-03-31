/*
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Feb 8, 2009
 * Time: 7:18:15 PM
 */
package edu.colorado.phet.movingman.ladybug.util

object TestBlock {
  def main(args: Array[String]) = {

    val block = defineAndInvoke {
      println("evaluated block")
    }

    block()
    block()
  }

  def defineAndInvoke(block: => Unit): () => Unit = {
    block
    block _
  }
}