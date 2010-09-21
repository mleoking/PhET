//package edu.colorado.phet.cckscala.tests
//
//import java.lang.Math._
//
//object TestRLCCircuit {
//  def main(args: Array[String]) {
//    var dynamicCircuit = new DynamicCircuit(Nil, Resistor(0, 1, 5.0) :: Nil, Capacitor(1, 2, 5E-6, 0, 5) :: Nil, Inductor(2, 0, 1, 20, 5) :: Nil)
//
//    val dt = 1E-4
//    //    var dynamicCircuit = circuit.getInitializedCircuit
//    println("init circuit=" + dynamicCircuit)
//    val v0 = dynamicCircuit.solve(dt).getVoltage(Resistor(1, 2, 10.0))
//    println("voltage")
//    for (i <- 0 until 10000) {
//      val t = i * dt
//      val comp = dynamicCircuit.getCompanionModel(dt)
//      val compSol = comp.circuit.solve
//      val solution = dynamicCircuit.solve(dt)
//      val voltage = solution.getVoltage(Resistor(0, 1, 10.0))
//      println(voltage)
//      dynamicCircuit = dynamicCircuit.stepInTime(dt)
//    }
//  }
//}
//
//object TestRLCircuit {
//  def main(args: Array[String]) {
//    val L = 1
//    val R = 10
//    val V = 5.0
//    val resistor = Resistor(1, 2, R)
//    val circuit = new DynamicCircuit(ResistiveBattery(0, 1, V, 0) :: Nil, resistor :: Nil, Nil, Inductor(2, 0, L, 0, 0) :: Nil)
//
//    val dt = 1E-4
//    var dynamicCircuit = circuit.getInitializedCircuit
//    println("init circuit=" + dynamicCircuit)
//    //    val v0 = dynamicCircuit.solve(dt).getVoltage(resistor)
//    println("voltage\tdesiredVoltage\terror")
//    for (i <- 0 until 1000) {
//      val t = i * dt
//      val comp = dynamicCircuit.getCompanionModel(dt)
//      //      println("companion=" + comp)
//      val compSol = comp.circuit.solve
//      //      println("companion sol=" + compSol)
//      val solution = dynamicCircuit.solve(dt)
//      val voltage = solution.getVoltage(resistor)
//      //see http://en.wikipedia.org/wiki/Lr_circuit
//      val desiredVoltage = -V * (1 - exp(-t * R / L)) //todo: why is negative sign here?
//      val error = abs(voltage - desiredVoltage)
//      println(voltage + "\t" + desiredVoltage + "\t" + error)
//
//      dynamicCircuit = dynamicCircuit.stepInTime(dt)
//    }
//  }
//}
//
//object TestParallelRCCircuit {
//  def main(args: Array[String]) {
//    val Vbatt = 10
//    val R = 10.0
//    val C = 1.0E-6
//    val battery = ResistiveBattery(0, 1, Vbatt, 0.0)
//    val resistor = Resistor(1, 2, R)
//    val resist2 = Resistor(2, 0, R)
//    val capacitor = Capacitor(2, 0, C, 0.0, 0.0)
//    val circuit = new DynamicCircuit(battery :: Nil, resistor :: resist2 :: Nil, capacitor :: Nil, Nil)
//    val inited = circuit.getInitializedCircuit
//    println("inited=" + inited)
//
//    val dt = 0.03E-6
//    var dynamicCircuit = inited
//    println("t\tvoltage\tdesiredVoltage\terror")
//    for (i <- 0 until 1000) {
//      val t = i * dt
//      val solutionAtT = dynamicCircuit.solve(dt)
//      val current = solutionAtT.getCurrent(battery)
//      val voltageAtT = solutionAtT.getVoltage(resistor)
//      val desiredVoltageAtT = -Vbatt * exp(-t / (R * C))
//      val error = voltageAtT - desiredVoltageAtT
//      //      println(t + "\t" + voltageAtT + "\t" + desiredVoltageAtT + "\t" + error)
//      println(current)
//
//
//
//      dynamicCircuit = dynamicCircuit.stepInTime(dt)
//    }
//  }
//
//
//}
//
//object TestRCCircuit {
//  def main(args: Array[String]) {
//    val Vbatt = 10
//    val R = 10.0
//    val C = 1.0E-6
//    val battery = ResistiveBattery(0, 1, Vbatt, 0)
//    val resistor = Resistor(1, 2, R)
//    val circuit = new DynamicCircuit(battery :: Nil, resistor :: Nil, Capacitor(2, 0, C, 0.0, 1.0) :: Nil, Nil)
////    val inited = circuit.getInitializedCircuit
////    println("inited=" + inited)
////    var dynamicCircuit = inited
//
//    var dynamicCircuit=circuit
//
//    val dt = 0.03E-6
//
//    println("t\tvoltage\tdesiredVoltage\terror")
//    for (i <- 0 until 1000) {
//      val t = i * dt
//      val solutionAtTPlusDT = dynamicCircuit.solve(dt)//solve gives solution at t+dt
//      val current = solutionAtTPlusDT.getCurrent(battery)
//      val voltageAtT = solutionAtTPlusDT.getVoltage(resistor)
//      val desiredVoltageAtTPlusDT = -Vbatt * exp(-(t+dt) / (R * C))
//      val error = voltageAtT - desiredVoltageAtTPlusDT
//      println( voltageAtT + "\t" + desiredVoltageAtTPlusDT + "\t" + error)
//
//
//
//      dynamicCircuit = dynamicCircuit.stepInTime(dt)
//    }
//  }
//}
