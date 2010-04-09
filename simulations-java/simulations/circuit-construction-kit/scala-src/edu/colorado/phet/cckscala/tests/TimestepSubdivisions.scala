package edu.colorado.phet.cckscala.tests

trait Steppable[A] {
  def distance(a: A, b: A): Double

  def update(a: A, dt: Double): A
}

class TimestepSubdivisions(errorThreshold: Double) {
  def update[A](originalState: A, steppable: Steppable[A], dt: Double) = {
    var state = originalState
    var elapsed = 0.0
    while (elapsed < dt) {
      var subdivisionDT = getTimestep(state, steppable, dt)
      if (subdivisionDT + elapsed > dt) subdivisionDT = dt - elapsed // don't exceed max allowed dt
      state = steppable.update(state, subdivisionDT)
      elapsed = elapsed + subdivisionDT
    }
    state
  }

  def getTimestep[A](state: A, steppable: Steppable[A], dt: Double): Double = {
    //store the previously used DT and try it first, then to increase it when possible.
    if (errorAcceptable(state, steppable, dt * 2))
      dt * 2 //only increase by one factor if this exceeds the totalDT, it will be cropped later
    else if (errorAcceptable(state, steppable, dt)) dt * 2
    else getTimestep(state, steppable, dt / 2)
  }

  def errorAcceptable[A](state: A, steppable: Steppable[A], dt: Double): Boolean = {
    if (dt < 1E-6)
      true
    else {
      val a = steppable.update(state, dt)
      val b1 = steppable.update(state, dt / 2)
      val b2 = steppable.update(b1, dt / 2)
      steppable.distance(a, b2) < errorThreshold
    }
  }
}

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
      state = new TimestepSubdivisions(1E-7).update(state, steppable, dt)
    }
  }
}