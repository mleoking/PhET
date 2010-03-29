package edu.colorado.phet.cckscala.tests

import collection.mutable.{HashSet, HashMap, ArrayBuffer}
import edu.colorado.phet.cckscala.tests._

class CompanionCircuit2(batteries: Seq[Battery], resistors: Seq[Resistor], currents: Seq[CurrentSource], capacitors: Seq[Capacitor], inductors: Seq[Inductor]) {
  def propagate(dt: Double) = {
    val (mnaCircuit, currentCompanions) = toMNACircuit(dt)
    new Solution1(this, mnaCircuit.solve, currentCompanions)
  }

  def updateWithSubdivisions(dt: Double) = {

  }

  def updateCircuit(solution: Solution1) = {
    val updatedCapacitors = for (c <- capacitors) yield
      new Capacitor(c.node0, c.node1, c.capacitance, solution.getNodeVoltage(c.node1) - solution.getNodeVoltage(c.node0), solution.getCurrent(c))
    new CompanionCircuit2(batteries, resistors, currents, updatedCapacitors, inductors)
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
      val battery = new Battery(c.node0, newNode, c.voltage - dt / 2.0 / c.capacitance * c.current) //TODO: explain the difference between this sign and the one in TestTheveninCapacitorRC
      val resistor = new Resistor(newNode, c.node1, dt / 2.0 / c.capacitance)
      companionBatteries += battery
      companionResistors += resistor
      //we need to be able to get the current for this component
      currentCompanions(c) = (s: Solution) => s.getCurrent(battery) //in series, so current is same through both companion components
    }

    //    for (i <- inductors) {
    //      mnaBatteries += new Battery
    //      mnaCurrents += new CurrentSource
    //    }
    (new Circuit(batteries ++ companionBatteries, resistors ++ companionResistors, currents ++ companionCurrents), currentCompanions)
  }
}

case class Solution1(circuit: CompanionCircuit2, mnaSolution: Solution, currentCompanions: HashMap[Element, Solution => Double]) {
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
    //    val resistance = 1E-6
    val resistance = 1
    val c = new Capacitor(2, 0, 0.1, 0.0, voltage / resistance)
    val battery = new Battery(0, 1, voltage)
    var circuit = new CompanionCircuit2(battery :: Nil, new Resistor(1, 2, resistance) :: Nil, Nil, c :: Nil, Nil)
    //    var circuit = new CompanionCircuit(battery :: Nil, new Resistor(1, 0, resistance) :: Nil, Nil, Nil, Nil)
    for (i <- 0 until 10) {
      val solution = circuit.propagate(0.03)
      //      println("solution = " + solution)
      println("current through battery = " + solution.getCurrent(battery))
      circuit = circuit.updateCircuit(solution)
    }
  }
}