package edu.colorado.phet.cckscala.tests

import collection.mutable.{HashMap, HashSet, ArrayBuffer}
import Jama.Matrix
import org.scalatest.FunSuite

import util.parsing.combinator.JavaTokenParsers

case class Solution(nodeVoltages: collection.Map[Int, Double], branchCurrents: collection.Map[(Int, Int), Double]) {
  def approxEquals(s: Solution, delta: Double) = {
    if (nodeVoltages.keySet != s.nodeVoltages.keySet || branchCurrents.keySet != s.branchCurrents.keySet)
      false
    else {
      val sameVoltages = nodeVoltages.keySet.foldLeft(true)((b: Boolean, a: Int) => {b && Math.abs(nodeVoltages(a) - s.nodeVoltages(a)) < delta})
      val sameCurrents = branchCurrents.keySet.foldLeft(true)((b: Boolean, a: (Int, Int)) => {b && Math.abs(branchCurrents(a) - s.branchCurrents(a)) < delta})
      sameVoltages && sameCurrents
    }
  }
}

trait Element {
  def node0: Int

  def node1: Int
}
case class Battery(node0: Int, node1: Int, voltage: Double) extends Element
case class Resistor(node0: Int, node1: Int, resistance: Double) extends Element
object TestMNA {
  class NodeVoltage {}
  class BranchCurrent {}
  def main(args: Array[String]) {
    println("testing")
    val netList = new Circuit(Array(Battery(0, 1, 4.0)), Array(Resistor(1, 0, 4.0)))
    val solution = netList.solve
    println(solution)
  }
}
case class Circuit(batteries: Seq[Battery], resistors: Seq[Resistor]) {
  def getNodeSet = {
    val set = new HashSet[Int]
    for (b <- batteries) {
      set += b.node0;
      set += b.node1
    }

    for (r <- resistors) {
      set += r.node0;
      set += r.node1
    }
    set
  }

  def getNodeCount = getNodeSet.size

  def getCurrentCount = batteries.length

  def getNumVars = getNodeCount + getCurrentCount

  case class Assignment(coefficient: Double, variable: Unknown) {
    def toTermString = {
      val prefix = if (coefficient == 1) "" else if (coefficient == -1) "-" else coefficient + "*"
      prefix + variable.toTermName
    }
  }
  class Equation(rhs: Double, assignments: Assignment*) {
    def stamp(row: Int, A: Matrix, z: Matrix, indexMap: Unknown => Int) = {
      z.set(row, 0, rhs)
      for (a <- assignments) A.set(row, indexMap(a.variable), a.coefficient)
    }

    override def toString = {
      val termList = for (a <- assignments) yield a.toTermString
      val result = "" + termList.mkString("+") + "=" + rhs
      result.replaceAll("\\+\\-", "\\-")
    }
  }
  abstract class Unknown {
    def toTermName: String
  }
  case class UnknownCurrent(startNode: Int, endNode: Int) extends Unknown {
    def toTermName = "I" + startNode + "_" + endNode
  }
  case class UnknownVoltage(node: Int) extends Unknown {
    def toTermName = "V" + node
  }
  def getCurrentConservationAssignments(node: Int) = {
    val nodeAssignments = new ArrayBuffer[Assignment]
    for (b <- batteries if b.node0 == node) nodeAssignments += Assignment(1, UnknownCurrent(b.node0, b.node1))
    for (b <- batteries if b.node1 == node) nodeAssignments += Assignment(-1, UnknownCurrent(b.node0, b.node1))
    for (r <- resistors if r.node0 == node) {
      nodeAssignments += Assignment(1 / r.resistance, UnknownVoltage(r.node0))
      nodeAssignments += Assignment(1 / r.resistance, UnknownVoltage(r.node1))
    }
    for (r <- resistors if r.node1 == node) {
      nodeAssignments += Assignment(-1 / r.resistance, UnknownVoltage(r.node0))
      nodeAssignments += Assignment(-1 / r.resistance, UnknownVoltage(r.node1))
    }
    nodeAssignments
  }

  def getEquations = {
    val list = new ArrayBuffer[Equation]
    //    println("nodeset=" + getNodeSet)
    //reference node has a voltage of 0.0
    list += new Equation(0, Assignment(1, UnknownVoltage(getNodeSet.toSeq(0))))
    //for each node, charge is conserved
    for (node <- getNodeSet) list += new Equation(0, getCurrentConservationAssignments(node): _*) //see p. 155 scala book
    //for each battery, voltage drop is given
    for (battery <- batteries) list += new Equation(battery.voltage, Assignment(-1, UnknownVoltage(battery.node0)), Assignment(1, UnknownVoltage(battery.node1)))
    list
  }

  def getUnknownVoltages = for (node <- getNodeSet) yield UnknownVoltage(node)

  def getUnknownCurrents = for (battery <- batteries) yield UnknownCurrent(battery.node0, battery.node1)

  def getUnknowns = { //todo: probably a way to do this in one line
    val list = new ArrayBuffer[Unknown]
    list ++= getUnknownVoltages
    list ++= getUnknownCurrents
    list
  }

  def solve = {
    var equations = getEquations

    val numVars = getNumVars
    val A = new Matrix(equations.size, getNumVars)
    val z = new Matrix(equations.size, 1)
    for (i <- 0 until equations.size) equations(i).stamp(i, A, z, getUnknowns.indexOf(_)) //todo: how to handle indexing reverse voltages
    val x = A.solve(z)

    val voltageMap = new HashMap[Int, Double]
    for (nodeVoltage <- getUnknownVoltages) voltageMap(nodeVoltage.node) = x.get(getUnknowns.indexOf(nodeVoltage), 0)

    val currentMap = new HashMap[(Int, Int), Double]
    for (currentVar <- getUnknownCurrents) currentMap((currentVar.startNode, currentVar.endNode)) = x.get(getUnknowns.indexOf(currentVar), 0)

    if (debug) {
      println(equations.mkString("\n"))
      println("a=")
      A.print(4, 2)
      println("z=")
      z.print(4, 2)
      println("unknowns=\n" + getUnknowns.mkString("\n"))
      println("x=")
      x.print(4, 2)
    }

    new Solution(voltageMap, currentMap)
  }

  var debug = false
}

class Tester extends FunSuite {
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
  test("current should be reversed when voltage is reversed") {
    val circuit = new Circuit(Array(Battery(0, 1, -4.0)), Array(Resistor(1, 0, 2.0)))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> -4.0), Map((0, 1) -> -2.0))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("Two batteries in parallel should have voltage added") {
    val circuit = new Circuit(Array(Battery(0, 1, -4.0), Battery(1, 2, -4.0)), Array(Resistor(2, 0, 2.0)))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> -4.0, 2 -> -8.0), Map((0, 1) -> -4, (1, 2) -> -4))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("Resistors in parallel should have harmonic mean of resistance") {
    val V = 9.0
    val R1 = 5.0
    val R2 = 5.0
    val Req = 1 / (1 / R1 + 1 / R2)
    val circuit = new Circuit(Array(Battery(0, 1, V)), Array(Resistor(1, 0, R1), Resistor(1, 0, R2)))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> V), Map((0, 1) -> V / Req))
    println("V=" + V + ", R1=" + R1 + ", R2=" + R2 + ", Req=" + Req)
    println("Actual Solution: " + circuit.solve)
    println("Desired Solution: " + desiredSolution)
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
}