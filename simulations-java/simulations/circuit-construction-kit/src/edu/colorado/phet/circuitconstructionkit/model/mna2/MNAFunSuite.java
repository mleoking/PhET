package edu.colorado.phet.circuitconstructionkit.model.mna2;

import junit.framework.TestCase;

public class MNAFunSuite extends TestCase{
    public void test1(){
        System.out.println("true = " + true);
    }
//  def approxEquals(a: Double, b: Double) = abs(a - b) <= 1E-6
//  test("battery resistor circuit should have correct voltages and currents for a simple circuit") {
//    val battery = Battery(0, 1, 4.0)
//    val circuit = new Circuit(battery :: Nil, Resistor(1, 0, 4.0) :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0), Map(battery -> 1.0))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("battery resistor circuit should have correct voltages and currents for a simple circuit ii") {
//    val battery = Battery(0, 1, 4.0)
//    val resistor = Resistor(1, 0, 2.0)
//    val circuit = new Circuit(battery :: Nil, resistor :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0), Map(battery -> 2.0))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("Should be able to obtain current for a resistor") {
//    val battery = Battery(0, 1, 4.0)
//    val resistor = Resistor(1, 0, 2.0)
//    val circuit = new Circuit(battery :: Nil, resistor :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0), Map(battery -> 2.0))
//    assert(circuit.solve.approxEquals(desiredSolution))
//    assert(approxEquals(circuit.solve.getCurrent(resistor), 2)) //current through resistor should be 2.0 Amps, same magnitude as battery: positive because current flows from node 1 to 0
//  }
//
//  //todo: works in IDE but fails in build process with error
////   found   : Double
////
//// required: scala.reflect.Manifest[?]
////
////      circuit.solve.getCurrent(Battery(4, 1, 999))
////                    ^
////  test("should throw an exception when asking for current for unknown element") {
////    val circuit = new Circuit(Battery(0, 1, 4.0) :: Nil, Resistor(1, 0, 2.0) :: Nil)
////    intercept(classOf[RuntimeException]) {
////      circuit.solve.getCurrent(Battery(4, 1, 999))
////    }
////  }
//  test("disjoint circuits should be solved independently") {
//    val battery = Battery(0, 1, 4)
//    val battery2 = Battery(2, 3, 5)
//    val circuit = new Circuit(battery :: battery2 :: Nil, Resistor(1, 0, 4.0) :: Resistor(3, 2, 2) :: Nil, Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4, 2 -> 0.0, 3 -> 5), Map(battery -> 1.0, battery2 -> 5.0 / 2.0))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("current source should provide current") {
//    val circuit = new Circuit(Nil, Resistor(1, 0, 4.0) :: Nil, CurrentSource(0, 1, 10.0) :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 10.0 * 4.0), Map())
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("current should be reversed when voltage is reversed") {
//    val battery = Battery(0, 1, -4.0)
//    val circuit = new Circuit(battery :: Nil, Resistor(1, 0, 2.0) :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> -4.0), Map(battery -> -2.0))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("Two batteries in series should have voltage added") {
//    val battery = Battery(0, 1, -4.0)
//    val battery2 = Battery(1, 2, -4.0)
//    val circuit = new Circuit(battery :: battery2 :: Nil, Resistor(2, 0, 2.0) :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> -4.0, 2 -> -8.0), Map(battery -> -4, battery2 -> -4))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("Two resistors in series should have resistance added") {
//    val battery = Battery(0, 1, 5.0)
//    val circuit = new Circuit(battery :: Nil, Resistor(1, 2, 10.0) :: Resistor(2, 0, 10.0) :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 5.0, 2 -> 2.5), Map(battery -> 5.0 / 20))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("A resistor with one node unconnected shouldn't cause problems") {
//    val battery = Battery(0, 1, 4.0)
//    val circuit = new Circuit(battery :: Nil, Resistor(1, 0, 4.0) :: Resistor(0, 2, 100.0) :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0, 2 -> 0.0), Map(battery -> 1.0))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("An unconnected resistor shouldn't cause problems") {
//    val battery = Battery(0, 1, 4.0)
//    val circuit = new Circuit(battery :: Nil, Resistor(1, 0, 4.0) :: Resistor(2, 3, 100.0) :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0, 2 -> 0.0, 3 -> 0.0), Map(battery -> 1.0))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("Should handle resistors with no resistance") {
//    val battery = Battery(0, 1, 5.0)
//    val resistor = Resistor(2, 0, 0.0)
//    val circuit = new Circuit(battery :: Nil, Resistor(1, 2, 10.0) :: resistor :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 5.0, 2 -> 0.0), Map(battery -> 5.0 / 10, resistor -> 5.0 / 10))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//  test("Resistors in parallel should have harmonic mean of resistance") {
//    val V = 9.0
//    val R1 = 5.0
//    val R2 = 5.0
//    val Req = 1 / (1 / R1 + 1 / R2)
//    val battery = Battery(0, 1, V)
//    val circuit = new Circuit(battery :: Nil, Resistor(1, 0, R1) :: Resistor(1, 0, R2) :: Nil)
//    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> V), Map(battery -> V / Req))
//    assert(circuit.solve.approxEquals(desiredSolution))
//  }
//
//  def testVRCCircuit(v: Double, r: Double, c: Double) = {
//    val resistor = Resistor(1, 2, r)
//    val circuit = new FullCircuit(ResistiveBattery(0, 1, v,0) :: Nil, resistor :: Nil, Capacitor(2, 0, c, 0.0, 0.0) :: Nil, Nil)
//
//    val dt = 1E-4
//    var dynamicCircuit = circuit.getInitializedCircuit
//    for (i <- 0 until 1000) { //takes 0.3 sec on my machine
//      val t = i * dt
//      val solutionAtTPlusDT = dynamicCircuit.solve(dt)
//      val voltage = solutionAtTPlusDT.getVoltage(resistor)
//      val desiredVoltageAtTPlusDT = -v * exp(-(t+dt) / r / c)
//      val error = abs(voltage - desiredVoltageAtTPlusDT)
//      assert(error < 1E-6) //sample run indicates largest error is 1.5328E-7, is this acceptable?  See TestRCCircuit
//      dynamicCircuit = dynamicCircuit.stepInTime(dt)
//    }
//  }
//  test("RC Circuit should have voltage exponentially decay with T=RC for v=5, r=10, c=1E-2") {
//    testVRCCircuit(5.0, 10.0, 1.0E-2)
//  }
//  test("RC Circuit should have voltage exponentially decay with T=RC for v=10, r=10, c=1E-2") {
//    testVRCCircuit(10.0, 10.0, 1.0E-2)
//  }
//  test("RC Circuit should have voltage exponentially decay with T=RC for v=3, r=7, c=1E-1") {
//    testVRCCircuit(3, 7, 1E-1)
//  }
//  test("RC Circuit should have voltage exponentially decay with T=RC for v=3, r=7, c=100") {
//    testVRCCircuit(3, 7, 100)
//  }
//  test("RL Circuit should have correct behavior for V=5 R=10 L=1") {
//    testVRLCircuit(5, 10, 1)
//  }
//  test("RL Circuit should have correct behavior for V=3 R=11 L=2.5") {
//    testVRLCircuit(3, 11, 2.5)
//  }
//  test("RL Circuit should have correct behavior for V=7 R=13 L=1E4") {
//    testVRLCircuit(7, 13, 1E4)
//  }
//  test("RL Circuit should have correct behavior for V=7 R=13 L=1E-4") {//todo: currently fails
//    testVRLCircuit(7, 13, 1E-4)
//  }
//  def testVRLCircuit(V: Double, R: Double, L: Double) {
//    val resistor = Resistor(1, 2, R)
//    val circuit = new FullCircuit(ResistiveBattery(0, 1, V,0) :: Nil, resistor :: Nil, Nil, Inductor(2, 0, L, 0, 0) :: Nil)
//    val dt = 1E-4
//    var dynamicCircuit = circuit.getInitializedCircuit
//    for (i <- 0 until 1000) {
//      val t = i * dt
//      val solution = dynamicCircuit.solve(dt)
//      val voltage = solution.getVoltage(resistor)
//      val desiredVoltage = -V * (1 - exp(-t * R / L)) //todo: why is negative sign here?
//      val error = abs(voltage - desiredVoltage)
//      //                  println(voltage + "\t" + desiredVoltage + "\t" + error)
//      assert(error < 1E-6)
//      dynamicCircuit = dynamicCircuit.stepInTime(dt)
//    }
//  }
}