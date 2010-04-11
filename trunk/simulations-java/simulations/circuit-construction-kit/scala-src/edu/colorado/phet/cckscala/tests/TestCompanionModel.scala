package edu.colorado.phet.cckscala.tests

import edu.colorado.phet.cckscala.tests.TestPrototype.CapacitorState

//This class verifies that the behavior of the prototype (which inlines the companion model) and the true companion models are identical.
object TestCompanionModel {
  def stepPrototype(vBattery: Double, rResistor: Double, c: Double, s: CapacitorState, totalDT: Double) = {
    TestPrototype.updateWithSubdivisions(vBattery, rResistor, c, s, totalDT).current
  }

  def stepCompanion(voltage: Double, resistance: Double, capacitance: Double, s: CapacitorState, totalDT: Double) = {
    val c = new Capacitor(2, 0, 0.1, s.voltage, s.current)
    val battery = new Battery(0, 1, voltage)
    var circuit = new DynamicCircuit(battery :: Nil, new Resistor(1, 2, resistance) :: Nil, Nil, c :: Nil, Nil)
    val solution = circuit.updateWithSubdivisions(totalDT)
    solution.capacitors(0).current
  }

  def main(args: Array[String]) {
    val voltage = 9.0
    //    val resistance = 1E-6
    val resistance = 1
    val capacitance = 0.1
    val c = new Capacitor(2, 0, capacitance, 0.0, voltage / resistance)
    val battery = new Battery(0, 1, voltage)
    val dt = 0.03

    var current = voltage / resistance
    var volts = 0.0
    for (i <- 0 until 10) {
      val currentThroughPrototype = stepPrototype(voltage, resistance, capacitance, new CapacitorState(volts, current), dt)
      val currentThroughCompanion = stepCompanion(voltage, resistance, capacitance, new CapacitorState(volts, current), dt)
      val diff = currentThroughPrototype - currentThroughCompanion
      println("i= " + i + ", diff = " + diff)

      if (diff.abs > 1E-10) throw new RuntimeException("mismatch: prototype = " + currentThroughPrototype + ", companion = " + currentThroughCompanion)

      current = currentThroughCompanion
      volts = voltage - current * resistance
    }

    //    var circuit = new CompanionCircuit(battery :: Nil, new Resistor(1, 2, resistance) :: Nil, Nil, c :: Nil, Nil)
    //    //    var circuit = new CompanionCircuit(battery :: Nil, new Resistor(1, 0, resistance) :: Nil, Nil, Nil, Nil)
    //    for (i <- 0 until 10) {
    //      val solution = circuit.propagate(0.03)
    //      //      println("solution = " + solution)
    //      println("current through battery = " + solution.getCurrent(battery))
    //      circuit = circuit.updateCircuit(solution)
    //    }
  }
}