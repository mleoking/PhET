package edu.colorado.phet.cckscala.tests.prototype2

import collection.mutable.{HashMap, ArrayBuffer}
import edu.colorado.phet.circuitconstructionkit.model.analysis.KirkhoffSolver.Equation

class MNACircuit(batteries: Seq[Battery], resistors: Seq[Resistor], currents: Seq[CurrentSource], currentCompanions: HashMap[Branch, Solution => Double]) {
  def getLinearSystem: LinearSystem = null
}
class LinearSystem(equations: Seq[Equation]) {
  def solve = new Solution
}
class Solution{
  def getCurrent(branch:Branch) = 0.0
}
trait Branch
case class Capacitor(node0: Int, node1: Int, capacitance: Double) extends Branch
case class CurrentSource(node0: Int, node1: Int, current: Double) extends Branch
case class Battery(node0: Int, node1: Int, voltage: Double) extends Branch
case class Resistor(node0: Int, node1: Int, resistance: Double) extends Branch
case class Inductor(node0: Int, node1: Int, inductance: Double) extends Branch
class Circuit1(batteries: Seq[Battery], resistors: Seq[Resistor], currents: Seq[CurrentSource], capacitors: Seq[Capacitor], inductors: Seq[Inductor]) {
  def solve = new Solution1(this, toMNACircuit)

  //why not give every component a companion in the MNACircuit?
  def toMNACircuit = {
    val mnaBatteries = new ArrayBuffer[Battery]
    for (b <- batteries) mnaBatteries += b

    val mnaResistors = new ArrayBuffer[Resistor]
    for (r <- resistors) mnaResistors += r

    val mnaCurrents = new ArrayBuffer[CurrentSource]
    for (c <- currents) mnaCurrents += c

    val currentCompanions = new HashMap[Branch, Solution => Double]

    //add companion models for capacitor
    for (c <- capacitors) {
      //in series
      val newNode = 100
      val battery = new Battery(c.node0, newNode, 2 / c.capacitance)
      val resistor = new Resistor(newNode, c.node1, 123)
      mnaBatteries += battery
      mnaResistors += resistor
      //we need to be able to get the current for this component
      currentCompanions(c) = ((s: Solution) => s.getCurrent(battery) + s.getCurrent(resistor))
    }

    //    for (i <- inductors) {
    //      mnaBatteries += new Battery
    //      mnaCurrents += new CurrentSource
    //    }

    new MNACircuit(batteries, resistors, currents, currentCompanions)
  }
}

class Solution1(circuit: Circuit1, mnaCircuit: MNACircuit) {
  val mnaSolution = mnaCircuit.getLinearSystem.solve

  def getNodeVoltage(node: Int) = 0.0

  def getCurrent(branch: Branch) = mnaSolution.getCurrent(branch)
}

object TestMNACompanion {
  def main(args: Array[String]) {
    val c = new Capacitor(0, 1, 0.1)
    val circuit = new Circuit1(new Battery(1, 0, 9.0) :: Nil, Nil, Nil,c::Nil,Nil)
    val solution = circuit.solve
    println("solution = " + solution)
    solution.getCurrent(c)
  }
}