/*
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Feb 10, 2009
 * Time: 11:08:59 PM
 */
package edu.colorado.phet.movingman.ladybug.test


class MyQueue[T](p: String) extends MovingManApplication(null) {
}
object TestSubtypes {
  def main(args: Array[String]) {
    println("main")

    val q = new MyQueue[String]("hello")
    println("out")
  }
}