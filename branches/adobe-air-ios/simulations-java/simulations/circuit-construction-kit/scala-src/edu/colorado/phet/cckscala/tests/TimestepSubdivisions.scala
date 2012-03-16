package edu.colorado.phet.cckscala.tests

import collection.mutable.ArrayBuffer

trait Steppable[A] {
  def distance(a: A, b: A): Double

  def update(a: A, dt: Double): A
}

case class Result[A](states:Seq[(Double,A)],state:A)

class TimestepSubdivisions(errorThreshold: Double) {
  def stepInTimeWithHistory[A](originalState: A, steppable: Steppable[A], dt: Double) = {
    var state = originalState
    var elapsed = 0.0
    val states = new ArrayBuffer[(Double,A)]
    while (elapsed < dt) {
      //println("elapsed = "+elapsed +", dt = "+dt)
      val seedValue = if (states.length>0) states(states.length-1)._1 else dt
      var subdivisionDT = getTimestep(state, steppable, seedValue)
      if (subdivisionDT + elapsed > dt) subdivisionDT = dt - elapsed // don't exceed max allowed dt
      state = steppable.update(state, subdivisionDT)
      states += ((subdivisionDT,state))
      elapsed = elapsed + subdivisionDT
    }
    Result(states.toList,state)
  }

  def stepInTime[A](originalState: A, steppable: Steppable[A], dt: Double) = stepInTimeWithHistory(originalState,steppable,dt).state

  def getTimestep[A](state: A, steppable: Steppable[A], dt: Double): Double = {
    //store the previously used DT and try it first, then to increase it when possible.
    if (dt < 1E-8) {
      println("Time step too small")
      dt
    }
    else if (errorAcceptable(state, steppable, dt * 2))
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