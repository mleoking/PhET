package edu.colorado.phet.cckscala.tests

import org.scalatest.FunSuite
import java.lang.Math._

class MNAFunSuite extends FunSuite {
  test("battery resistor circuit should have correct voltages and currents for a simple circuit") {
    val circuit = new Circuit(Array(Battery(0, 1, 4.0)), Array(Resistor(1, 0, 4.0)))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0), Map((0, 1) -> 1.0))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("battery resistor circuit should have correct voltages and currents for a simple circuit ii") {
    val circuit = new Circuit(Array(Battery(0, 1, 4.0)), Array(Resistor(1, 0, 2.0)))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0), Map((0, 1) -> 2.0))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("disjoint circuits should be solved independently") {
    val circuit = new Circuit(Battery(0, 1, 4) :: Battery(2, 3, 5) :: Nil, Resistor(1, 0, 4.0) :: Resistor(3, 2, 2) :: Nil, Nil)
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4, 2 -> 0.0, 3 -> 5), Map((0, 1) -> 1.0, (2, 3) -> 5.0 / 2.0))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("current source should provide current") {
    val circuit = new Circuit(Nil, Resistor(1, 0, 4.0) :: Nil, CurrentSource(0, 1, 10.0) :: Nil)
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 10.0 * 4.0), Map())
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("current should be reversed when voltage is reversed") {
    val circuit = new Circuit(Array(Battery(0, 1, -4.0)), Array(Resistor(1, 0, 2.0)))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> -4.0), Map((0, 1) -> -2.0))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("Two batteries in series should have voltage added") {
    val circuit = new Circuit(Array(Battery(0, 1, -4.0), Battery(1, 2, -4.0)), Array(Resistor(2, 0, 2.0)))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> -4.0, 2 -> -8.0), Map((0, 1) -> -4, (1, 2) -> -4))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("Two resistors in series should have resistance added") {
    val circuit = new Circuit(Battery(0, 1, 5.0) :: Nil, Resistor(1, 2, 10.0) :: Resistor(2, 0, 10.0) :: Nil)
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 5.0, 2 -> 2.5), Map((0, 1) -> 5.0 / 20))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("A resistor hanging off the edge shouldn't cause problems") {
    val circuit = new Circuit(Array(Battery(0, 1, 4.0)), Array(Resistor(1, 0, 4.0), Resistor(0, 2, 100.0)))
    //    println("equations:\n" + circuit.getEquations.mkString("\n"))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0, 2 -> 0.0), Map((0, 1) -> 1.0))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("Should handle resistors with no resistance") {
    val circuit = new Circuit(Battery(0, 1, 5.0) :: Nil, Resistor(1, 2, 10.0) :: Resistor(2, 0, 0.0) :: Nil)
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 5.0, 2 -> 0.0), Map((0, 1) -> 5.0 / 10, (2, 0) -> 5.0 / 10))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("Resistors in parallel should have harmonic mean of resistance") {
    val V = 9.0
    val R1 = 5.0
    val R2 = 5.0
    val Req = 1 / (1 / R1 + 1 / R2)
    val circuit = new Circuit(Array(Battery(0, 1, V)), Array(Resistor(1, 0, R1), Resistor(1, 0, R2)))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> V), Map((0, 1) -> V / Req))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("RC Circuit should have voltage exponentially decay with T=RC") {
    val circuit = new FullCircuit(Battery(0, 1, 5.0) :: Nil, Resistor(1, 2, 10.0) :: Nil, Capacitor(2, 0, 1.0E-2, 0.0, 0.0) :: Nil, Nil)
    val v0 = -5 //todo: make sure in sync with inited circuit

    val dt = 1E-4
    var dynamicCircuit = circuit.getInitializedCircuit
    for (i <- 0 until 10000) { //takes 3 sec on my machine
      val t = i * dt
      val solution = dynamicCircuit.solve(dt)
      val voltage = solution.getVoltage(Resistor(1, 2, 10.0))
      val desiredVoltage = v0 * exp(-t / 10.0 / 1.0E-2)
      val error = abs(voltage - desiredVoltage)
      assert(error < 1E-6) //sample run indicates largest error is 1.5328E-7, is this acceptable?  See TestRCCircuit
      dynamicCircuit = dynamicCircuit.stepInTime(dt)
    }
  }
  //  test("RL Circuit should have voltage exponentially decay with T=RC") {
  //    val L = 5
  //    val circuit = new FullCircuit(Battery(0, 1, 5.0) :: Nil, Resistor(1, 2, 10.0) :: Nil, Nil, Inductor(2, 0, L, 0, 0) :: Nil)
  //
  //    val dt = 1E-4
  //    var dynamicCircuit = circuit.getInitializedCircuit
  //    val v0 = dynamicCircuit.solve(dt).getVoltage(Resistor(1, 2, 10.0))
  //    for (i <- 0 until 10000) { //takes 3 sec on my machine
  //      val t = i * dt
  //      val solution = dynamicCircuit.solve(dt)
  //      val voltage = solution.getVoltage(Resistor(1, 2, 10.0))
  //      val desiredVoltage = v0 * exp(-t / 10.0 / 1.0E-2)
  //      val error = abs(voltage - desiredVoltage)
  //      assert(error < 1E-6) //sample run indicates largest error is 1.5328E-7, is this acceptable?  See TestRCCircuit
  //      dynamicCircuit = dynamicCircuit.stepInTime(dt)
  //    }
  //  }
}