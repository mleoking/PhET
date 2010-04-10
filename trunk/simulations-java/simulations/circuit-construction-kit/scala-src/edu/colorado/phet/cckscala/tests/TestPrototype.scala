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
    val steppable = new Steppable[State] {
      def update(a: State, dt: Double) = TestPrototype.update(voltage, resistance, capacitance, a, dt)

      def distance(a: State, b: State) = MathUtil.euclideanDistance(a.i :: Nil, b.i :: Nil)//a.distance(b)//TODO: improve distance metric; just using current euclidean for comparison for TestCompanionModel
    }
    new TimestepSubdivisions(1E-7).update(originalState, steppable, dt)
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

  //voltage and current across the capacitor
  case class State(v: Double, i: Double, dt: Double, time: Double) {
    def distance(s: State) = Math.sqrt(square(s.v - v) + square(s.i - i)) / 2

    def square(x: Double) = x * x
  }
}