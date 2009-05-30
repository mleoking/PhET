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
case class CurrentSource(node0: Int, node1: Int, current: Double) extends Element
case class CompanionModel(batteries: Seq[Battery], resistors: Seq[Resistor], currentSources: Seq[CurrentSource])

case class Capacitor(node0: Int, node1: Int, capacitance: Double, voltage: Double, current: Double) extends Element {
  def getCompanionModel(dt: Double, newNode: () => Int) = {
    //linear companion model for capacitor, using trapezoidal approximation, under thevenin model, see http://dev.hypertriton.com/edacious/trunk/doc/lec.pdf
    val midNode = newNode()
    new CompanionModel(new Battery(node0, midNode, voltage + dt * current / 2 / capacitance) :: Nil,
      new Resistor(midNode, node1, dt / 2 / capacitance) :: Nil, Nil)
  }
}

case class Inductor(node0: Int, node1: Int, inductance: Double, voltage: Double, current: Double) extends Element {
  def getCompanionModel(dt: Double, newNode: () => Int) = {
    //linear companion model for inductor, using trapezoidal approximation, under norton model, see http://dev.hypertriton.com/edacious/trunk/doc/lec.pdf
    //    val midNode = newNode()
    new CompanionModel(Nil,
      new Resistor(node0, node1, dt / 2 / inductance) :: Nil, new CurrentSource(node0, node1, current + dt * voltage / 2 / inductance) :: Nil)
  }
}

case class FullCircuit(batteries: Seq[Battery], resistors: Seq[Resistor], capacitors: Seq[Capacitor], inductors: Seq[Inductor]) extends AbstractCircuit {
  def getCompanionCircuit(dt: Double) = {
    val b = new ArrayBuffer[Battery]
    b ++= batteries
    val r = new ArrayBuffer[Resistor]
    r ++= resistors
    val cs = new ArrayBuffer[CurrentSource]

    val usedIndices = new ArrayBuffer[Int]

    for (c <- capacitors) {
      val cm = c.getCompanionModel(dt, () => getFreshIndex(usedIndices))
      for (battery <- cm.batteries) b += battery
      for (resistor <- cm.resistors) r += resistor
      for (currentSource <- cm.currentSources) cs += currentSource
    }
    new Circuit(b, r)
  }

  //Find the first node index that is unused in the node set or used indices, and update the used indices
  def getFreshIndex(usedIndices: ArrayBuffer[Int]) = {
    var selected = -1
    var testIndex = 0
    while (selected == -1) {
      if (!getNodeSet.contains(testIndex) && !usedIndices.contains(testIndex)) {
        selected = testIndex
      }
      testIndex = testIndex + 1
    }
    usedIndices += selected
    selected
  }

  def getElements = {
    val elm = new ArrayBuffer[Element]
    elm ++= batteries
    elm ++= resistors
    elm ++= capacitors
    elm ++= inductors
    elm
  }

  def solve = {
    val companionCircuit = getCompanionCircuit(1E-4)
    val solution = companionCircuit.solve

    //    val solution = getCompanionCircuit.solve
    //todo: provide mapping
    solution
  }
}

trait AbstractCircuit {
  def getNodeSet = {
    val set = new HashSet[Int]
    for (element <- getElements) {
      set += element.node0
      set += element.node1
    }
    set
  }

  def getElements: Seq[Element]
}

case class Circuit(batteries: Seq[Battery], resistors: Seq[Resistor]) extends AbstractCircuit {
  //  def this(batteries: Seq[Battery], resistors: Seq[Resistor]) = this (batteries, resistors, Nil);

  def getElements = {
    val elements = new ArrayBuffer[Element]
    elements ++= batteries
    elements ++= resistors
    elements
  }

  def getNodeCount = getNodeSet.size

  def getCurrentCount = batteries.length

  def getNumVars = getNodeCount + getCurrentCount

  case class Term(coefficient: Double, variable: Unknown) {
    def toTermString = {
      val prefix = if (coefficient == 1) "" else if (coefficient == -1) "-" else coefficient + "*"
      prefix + variable.toTermName
    }
  }
  class Equation(rhs: Double, terms: Term*) {
    def stamp(row: Int, A: Matrix, z: Matrix, indexMap: Unknown => Int) = {
      z.set(row, 0, rhs)
      for (a <- terms) A.set(row, indexMap(a.variable), a.coefficient + A.get(row, indexMap(a.variable)))
    }

    override def toString = {
      val termList = for (a <- terms) yield a.toTermString
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
  def getCurrentConservationTerms(node: Int) = {
    val nodeTerms = new ArrayBuffer[Term]
    for (b <- batteries if b.node0 == node) nodeTerms += Term(1, UnknownCurrent(b.node0, b.node1))
    for (b <- batteries if b.node1 == node) nodeTerms += Term(-1, UnknownCurrent(b.node0, b.node1))
    for (r <- resistors if r.node0 == node) {
      nodeTerms += Term(1 / r.resistance, UnknownVoltage(r.node0))
      nodeTerms += Term(1 / r.resistance, UnknownVoltage(r.node1))
    }
    for (r <- resistors if r.node1 == node) {
      nodeTerms += Term(-1 / r.resistance, UnknownVoltage(r.node0))
      nodeTerms += Term(-1 / r.resistance, UnknownVoltage(r.node1))
    }
    nodeTerms
  }

  def getEquations = {
    val list = new ArrayBuffer[Equation]
    //    println("nodeset=" + getNodeSet)

    //reference node has a voltage of 0.0
    //todo: should have one zero node per strong component
    list += new Equation(0, Term(1, UnknownVoltage(getNodeSet.toSeq(0))))

    //for each node, charge is conserved
    for (node <- getNodeSet) list += new Equation(0, getCurrentConservationTerms(node): _*) //see p. 155 scala book

    //for each battery, voltage drop is given
    for (battery <- batteries) list += new Equation(battery.voltage, Term(-1, UnknownVoltage(battery.node0)), Term(1, UnknownVoltage(battery.node1)))
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
      println("Debugging circuit: " + toString)
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

object TestMNA {
  def main(args: Array[String]) {
    val V = 9.0
    val R1 = 5.0
    val R2 = 5.0
    val Req = 1 / (1 / R1 + 1 / R2)
    //todo: need to compute the initial voltage and current across capacitor
    //could pretend it is a resistor with no resistance, and compute voltage drop (0.0) and current
    //similar for inductors
    val circuit = new FullCircuit(Array(Battery(0, 1, V)), Array(Resistor(1, 2, R1)), Array(Capacitor(2, 0, 1.0, 0.0, 0.0)), Nil)
    val companion = circuit.getCompanionCircuit(1E-4)
    println("companion=" + companion)
    val solution = circuit.solve
    println("solution=" + solution)
    //    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> V), Map((0, 1) -> V / Req))
    //    println("V=" + V + ", R1=" + R1 + ", R2=" + R2 + ", Req=" + Req)
    //    println("Actual Solution: " + circuit.solve)
    //    println("Desired Solution: " + desiredSolution)
    //    circuit.debug = true
    //    circuit.solve
    null
  }
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
  test("A resistor hanging off the edge shouldn't cause problems") {
    val circuit = new Circuit(Array(Battery(0, 1, 4.0)), Array(Resistor(1, 0, 4.0), Resistor(0, 2, 100.0)))
    println("equations:\n" + circuit.getEquations.mkString("\n"))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0, 2 -> 0.0), Map((0, 1) -> 1.0))
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