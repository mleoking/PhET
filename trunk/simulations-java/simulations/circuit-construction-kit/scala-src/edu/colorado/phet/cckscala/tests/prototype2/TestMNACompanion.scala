package edu.colorado.phet.cckscala.tests.prototype2

import collection.mutable.{HashSet, HashMap, ArrayBuffer}
import edu.colorado.phet.cckscala.tests._

class CompanionCircuit(batteries: Seq[Battery], resistors: Seq[Resistor], currents: Seq[CurrentSource], capacitors: Seq[Capacitor], inductors: Seq[Inductor]) {
  def solve(dt: Double) = {
    val (mnaCircuit, currentCompanions) = toMNACircuit(dt)
    new Solution1(this, mnaCircuit.solve, currentCompanions)
  }
  //
  def update(dt: Double) = {
    val solution = solve(dt)
    val updatedCapacitors = for (c <- capacitors) yield
      new Capacitor(c.node0, c.node1, c.capacitance, solution.getNodeVoltage(c.node1) - solution.getNodeVoltage(c.node0), solution.getCurrent(c))
    new CompanionCircuit(batteries, resistors, currents, updatedCapacitors, inductors)
  }

  //why not give every component a companion in the MNACircuit?
  def toMNACircuit(dt: Double) = {
    val companionBatteries = new ArrayBuffer[Battery]
    val companionResistors = new ArrayBuffer[Resistor]
    val companionCurrents = new ArrayBuffer[CurrentSource]
    val currentCompanions = new HashMap[Element, Solution => Double]

    val usedNodes = new HashSet[Int]
    val allElements: Seq[Element] = batteries.toList ++ resistors.toList ++ currents.toList ++ capacitors.toList ++ inductors.toList
    for (b <- allElements) {
      usedNodes += b.node0
      usedNodes += b.node1
    }
    def max(m: List[Int]) = m.sortWith(_ < _).reverse.head

    //add companion models for capacitor

    //TRAPEZOIDAL
    //        double vc = state.v + dt / 2 / c * state.i;
    //        double rc = dt / 2 / c;

    //BACKWARD EULER
    //        double vc = state.v;
    //        double rc = dt / c;

    for (c <- capacitors) {
      //in series
      val newNode = max(usedNodes.toList) + 1
      usedNodes += newNode
      val battery = new Battery(c.node0, newNode, c.voltage + dt / 2.0 / c.capacitance * c.current)
      val resistor = new Resistor(newNode, c.node1, dt / 2.0 / c.capacitance)
      companionBatteries += battery
      companionResistors += resistor
      //we need to be able to get the current for this component
      currentCompanions(c) = (s: Solution) => s.getCurrent(battery)
    }

    //    for (i <- inductors) {
    //      mnaBatteries += new Battery
    //      mnaCurrents += new CurrentSource
    //    }
    (new Circuit(batteries ++ companionBatteries, resistors ++ companionResistors, currents ++ companionCurrents), currentCompanions)
  }
}

case class Solution1(circuit: CompanionCircuit, mnaSolution: Solution, currentCompanions: HashMap[Element, Solution => Double]) {
  def getNodeVoltage(node: Int) = mnaSolution.getNodeVoltage(node)

  def getCurrent(element: Element) = {
    if (currentCompanions.contains(element))
      currentCompanions(element)(mnaSolution)
    else
      mnaSolution.getCurrent(element)
  }

}

object TestMNACompanion {
  def main(args: Array[String]) {
    val voltage = 9.0
    val resistance = 1E-6
    val c = new Capacitor(2, 0, 0.1, 0.0, voltage / resistance)
    val battery = new Battery(0, 1, 9.0)
    var circuit = new CompanionCircuit(battery :: Nil, new Resistor(1, 2, resistance) :: Nil, Nil, c :: Nil, Nil)
    val solution = circuit.solve(0.03)
    println("solution = " + solution)
    //    solution.getCurrent(c)
    solution.getCurrent(battery)

    for (i <- 0 until 10) {
      circuit = circuit.update(0.03)
      println("current through battery = " + circuit.solve(0.03).getCurrent(battery))
    }
  }
}