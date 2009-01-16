package edu.colorado.phet.movingman.ladybug

object TestSetter {
  def main(args: Array[String]) = {
    class Time{
      var h=12
      def hour:Int=h
      def hour_=(x:Int)={h=x}
    }
    val t=new Time
    t.hour = 123
  }
}