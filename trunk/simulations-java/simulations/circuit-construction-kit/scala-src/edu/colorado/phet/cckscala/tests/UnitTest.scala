package edu.colorado.phet.cckscala.tests

import TestPrototype.State
import org.scalatest.FunSuite

class UnitTest extends FunSuite {
  def approxEquals(a: Double, b: Double) = (a - b).abs <= 1E-6
  test("prototype should match companion model for one time step with resistance = 1.0") {testComparison(voltage = 9.0, resistance = 1.0, capacitance = 0.1, dt = 0.03)}
  test("prototype should match companion model for one time step with resistance = 1E-6") {testComparison(voltage = 9.0, resistance = 1E-6, capacitance = 0.1, dt = 0.03)}

  test("prototype should match companion model for 2nd time step") {
    testComparison(9.0, 1.0, 0.1, 0.03, 6.667349335988726, 2.332650664011274) //current and voltage obtained from prototype TestPrototype
  }

  def testComparison(voltage: Double, resistance: Double, capacitance: Double, dt: Double): Unit = {
    testComparison(voltage, resistance, capacitance, dt, voltage / resistance, 0.0)
  }

  def testComparison(voltage: Double, resistance: Double, capacitance: Double, dt: Double, initialCurrent: Double, initialVoltage: Double): Unit = {
    val currentThroughPrototype = TestPrototype.updateWithSubdivisions(voltage, resistance, capacitance, new State(initialVoltage, initialCurrent, dt, 0.0), dt).i
    val currentThroughCompanion = {
      val c = new Capacitor(2, 0, capacitance, initialVoltage, initialCurrent)
      val battery = new Battery(0, 1, voltage)
      var circuit = new DynamicCircuit(battery :: Nil, new Resistor(1, 2, resistance) :: Nil, Nil, c :: Nil, Nil)
      val solution = circuit.updateWithSubdivisions(dt)
      solution.capacitors(0).current
    }
    println("prototype: " + currentThroughPrototype + ", companion = " + currentThroughCompanion)
    assert(approxEquals(currentThroughPrototype, currentThroughCompanion))
  }
}