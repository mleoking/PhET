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

    var state = new State(0, vBattery / rResistor, dt, t)

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

  def updateWithSubdivisions(voltage: Double, resistance: Double, capacitance: Double, originalState: State, dt: Double) = {
    var state = originalState
    val startTime = state.time
    val endTime = startTime + dt
    while (state.time < endTime) { //run a number of dt's so that totalDT elapses in the end
      var subdivisionDT = getTimestep(voltage, resistance, capacitance, state, state.dt)
      if (subdivisionDT + state.time > endTime) subdivisionDT = endTime-state.time //don't overshoot the specified total
      state = update(voltage, resistance, capacitance, state, subdivisionDT)
    }
    state
  }

  def getTimestep(voltage: Double, resistance: Double, capacitance: Double, state: State, dt: Double): Double = {
    //store the previously used DT and try it first, then to increase it when possible.
    if (errorAcceptable(voltage, resistance, capacitance, state, dt * 2))
      dt * 2 //only increase by one factor if this exceeds the totalDT, it will be cropped later
    else if (errorAcceptable(voltage, resistance, capacitance, state, dt)) dt * 2
    else getTimestep(voltage, resistance, capacitance, state, dt / 2)
  }

  def errorAcceptable(voltage: Double, resistance: Double, capacitance: Double, state: State, dt: Double): Boolean = {
    if (dt < 1E-6)
      true
    else {
      val a = update(voltage, resistance, capacitance, state, dt)
      val b1 = update(voltage, resistance, capacitance, state, dt / 2)
      val b2 = update(voltage, resistance, capacitance, b1, dt / 2)
      a.distance(b2) < 1E-7
    }
  }

  def update(voltage: Double, resistance: Double, capacitance: Double, state: State, dt: Double) = {
    //TRAPEZOIDAL
    val vc = state.v + dt / 2 / capacitance * state.i
    val rc = dt / 2 / capacitance

    //BACKWARD EULER
    //    val vc = state.v
    //    val rc = dt / c

    val newCurrent = (voltage - vc) / (rc + resistance)
    val newVoltage = voltage - newCurrent * resistance //signs may be wrong here

    new State(newVoltage, newCurrent, dt, state.time + dt)
  }

  case class State(v: Double, i: Double, dt: Double, time: Double) {
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