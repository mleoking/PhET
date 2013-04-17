package edu.colorado.phet.cckscala.tests

object TestTimestepSubdivisions {
  case class TestState(time: Double = 0.0)
  def main(args: Array[String]) {
    val originalState = new TestState
    val steppable = new Steppable[TestState] {
      def update(a: TestState, dt: Double) = new TestState(a.time + dt)

      def distance(a: TestState, b: TestState) = (a.time - b.time).abs
    }
    val dt = 1.0
    var state = originalState
    for (i <- 0 until 10) {
      println("state = " + state)
      state = new TimestepSubdivisions(1E-7).stepInTime(state, steppable, dt)
    }
  }
}