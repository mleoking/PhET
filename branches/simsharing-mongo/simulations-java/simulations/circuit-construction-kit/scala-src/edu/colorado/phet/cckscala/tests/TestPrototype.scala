package edu.colorado.phet.cckscala.tests

import java.text.DecimalFormat
import java.io.{File, FileWriter, BufferedWriter}

object TestPrototype {
  def updateWithSubdivisions(voltage: Double, resistance: Double, capacitance: Double, capacitorState: CapacitorState, dt: Double) = {
    val steppable = new Steppable[CapacitorState] {
      def update(a: CapacitorState, dt: Double) = TestPrototype.update(voltage, resistance, capacitance, a, dt)

      def distance(a: CapacitorState, b: CapacitorState) = MathUtil.euclideanDistance(a.current :: Nil, b.current :: Nil) //a.distance(b)//TODO: improve distance metric; just using current euclidean for comparison for TestCompanionModel
    }
    new TimestepSubdivisions(1E-7).stepInTime(capacitorState, steppable, dt)
  }

  def update(voltage: Double, resistance: Double, capacitance: Double, state: CapacitorState, dt: Double) = {
    //TRAPEZOIDAL
    val companionBatteryVoltage = state.voltage + dt / 2 / capacitance * state.current
    val companionResistorResistance = dt / 2 / capacitance

    //    println("companion resistor resistance = "+companionResistorResistance+", companion battery voltage = "+companionBatteryVoltage)

    //BACKWARD EULER
    //    val companionBatteryVoltage = state.voltage
    //    val companionResistorResistance = dt / capacitance

    val newCurrent = (voltage - companionBatteryVoltage) / (companionResistorResistance + resistance)
    val newVoltage = voltage - newCurrent * resistance //signs may be wrong here

    new CapacitorState(newVoltage, newCurrent)
  }

  //voltage and current across the capacitor
  case class CapacitorState(voltage: Double, current: Double)

  def main(args: Array[String]) {
    val f = new DecimalFormat("0.000000000000000")
    val voltage = 9
    //    val resistance = 1
    val resistance = 1E-6
    val capacitance = 0.1
    var time = 0.0
    val dt = 0.03
    var state = new CapacitorState(0, voltage / resistance)

    val headers = "iteration \t dt \t t \t v(t) \t i(t) \t vTrue \t vNumerical \t error"
    println(headers)

    val bufferedWriter = new BufferedWriter(new FileWriter(new File("C:/Users/Owner/Desktop/cck-out" + System.currentTimeMillis() + ".txt")))
    bufferedWriter.write(headers + "\n")

    for (j <- 0 until 15) {
      val vTrue = voltage * Math.exp(-time / resistance / capacitance)
      val vNumeric = voltage - state.voltage

      val error = Math.abs(vTrue - vNumeric)
      val str = j + "\t" + f.format(dt) + "\t" + f.format(time) + "\t" + f.format(state.voltage) + "\t" + f.format(state.current) + "\t" + f.format(vTrue) + "\t" + f.format(vNumeric) + "\t" + f.format(error)
      println(str)
      bufferedWriter.write(str + "\n")

      state = updateWithSubdivisions(voltage, resistance, capacitance, state, dt)
      time = time + dt
    }
    bufferedWriter.close()
  }
}