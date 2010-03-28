package edu.colorado.phet.cckscala.tests

import java.text.DecimalFormat
import java.io.{File, FileWriter, BufferedWriter}
import collection.mutable.HashMap

object TestPrototype {
  def main(args: Array[String]) {
    val f = new DecimalFormat("0.000000000000000")
    val vBattery = 9
    val rResistor = 1
    //    val rResistor = 1E-6
    val c = 0.1

    var t = 0.0
    val dt = 0.03

    var state = new State(0, vBattery / rResistor, dt)

    val headers = "iteration \t dt \t t \t v(t) \t i(t) \t vTrue \t vNumerical \t error"
    println(headers)

    val bufferedWriter = new BufferedWriter(new FileWriter(new File("C:/Users/Sam/Desktop/cck-out" + System.currentTimeMillis() + ".txt")))
    bufferedWriter.write(headers + "\n")

    for (j <- 0 until 15) {
      val vTrue = vBattery * Math.exp(-t / rResistor / c)
      val vNumeric = vBattery - state.v

      val error = Math.abs(vTrue - vNumeric)
      val str = j + "\t" + f.format(dt) + "\t" + f.format(t) + "\t" + f.format(state.v) + "\t" + f.format(state.i) + "\t" + f.format(vTrue) + "\t" + f.format(vNumeric) + "\t" + f.format(error)
      println(str)
      bufferedWriter.write(str + "\n")

      state = updateWithSubdivisions(vBattery, rResistor, c, state, dt)
      t = t + dt
    }
    bufferedWriter.close()
  }

  def updateWithSubdivisions(vBattery: Double, rResistor: Double, c: Double, s: State, totalDT: Double) = {
    var state = s
    var elapsed = 0.0
    var numSubdivisions = 0
    //run a number of dt's so that totalDT elapses in the end
    while (elapsed < totalDT) {
      val resultCache = new ResultCache(vBattery, rResistor, c) //to prevent recomputation of updates
      var dt = getTimestep(vBattery, rResistor, c, state, state.dt, resultCache)
      if (dt + elapsed > totalDT) dt = totalDT - elapsed //don't overshoot the specified total
      state = resultCache.update(state, dt)
      elapsed = elapsed + dt
      numSubdivisions = numSubdivisions + 1
      //            System.out.println("picked dt = "+dt)
    }
    //        System.out.println("num subdivisions = "+numSubdivisions)
    state
  }

  def getTimestep(vBattery: Double, rResistor: Double, c: Double, state: State, dt: Double, resultCache: ResultCache): Double = {
    //store the previously used DT and try it first, then to increase it when possible.
    if (errorAcceptable(vBattery, rResistor, c, state, dt * 2, resultCache))
      dt * 2 //only increase by one factor if this exceeds the totalDT, it will be cropped later
    else if (errorAcceptable(vBattery, rResistor, c, state, dt, resultCache)) dt * 2
    else getTimestep(vBattery, rResistor, c, state, dt / 2, resultCache)
  }

  def errorAcceptable(vBattery: Double, rResistor: Double, c: Double, state: State, dt: Double, cache: ResultCache): Boolean = {
    if (dt < 1E-6)
      true
    else {
      val a = cache.update(state, dt)
      val b1 = cache.update(state, dt / 2)
      val b2 = cache.update(b1, dt / 2)
      a.distance(b2) < 1E-7
    }
  }

  def update(vBattery: Double, rResistor: Double, c: Double, state: State, dt: Double) = {
    //TRAPEZOIDAL
    val vc = state.v + dt / 2 / c * state.i
    val rc = dt / 2 / c

    //BACKWARD EULER
    //    val vc = state.v
    //    val rc = dt / c

    val newCurrent = (vBattery - vc) / (rc + rResistor)
    val newVoltage = vBattery - newCurrent * rResistor //signs may be wrong here

    new State(newVoltage, newCurrent, dt)
  }

  case class State(v: Double, i: Double, dt: Double) {
    def distance(s: State) = Math.sqrt(square(s.v - v) + square(s.i - i)) / 2

    def square(x: Double) = x * x
  }

  case class Key(dt: Double, state: State)

  //TODO: Reuse the computations of update between error checks, and return one of the intermediate states instead of recomputing once dt has been accepted.
  case class ResultCache(vBattery: Double, rResistor: Double, c: Double) {
    val cache = new HashMap[Key, State]

    def update(state: State, dt: Double): State = {
      val key = new Key(dt, state)
      if (cache.contains(key)) cache.get(key).get
      else {
        val result = TestPrototype.update(vBattery, rResistor, c, state, dt)
        cache(key) = result
        result
      }
    }
  }
}