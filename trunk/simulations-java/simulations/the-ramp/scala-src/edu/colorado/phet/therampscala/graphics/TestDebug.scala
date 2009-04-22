package edu.colorado.phet.therampscala.graphics

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Apr 22, 2009
 * Time: 10:28:49 AM
 * To change this template use File | Settings | File Templates.
 */

object TestDebug {
  def main(args: Array[String]) {
    object debug {
      def eval[T](a: => T) = {
        val result = a
        println("obtained: " + a)
        result
      }
    }
    object model {
      def setValue(a: Double) = {}
    }
    def brokenMethod() = 4 * 3
    model.setValue(debug eval brokenMethod)
  }
}