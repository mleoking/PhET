package edu.colorado.phet.cckscala.tests

import collection.mutable.{HashMap, HashSet, ArrayBuffer}
import Jama.Matrix
import org.scalatest.FunSuite
import java.lang.Math._

import util.parsing.combinator.JavaTokenParsers

//sparse solution containing only the solved unknowns in MNA
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

  //  def toFormattedString = {
  //    val stringBuffer = new StringBuffer
  //    stringBuffer.append("Solution(")
  //  }

  def getVoltage(e: Element) = nodeVoltages(e.node1) - nodeVoltages(e.node0)

  def getCurrent(e: Element) = {
    //if it was a battery or resistor (of R=0), look up the answer
    if (branchCurrents.contains((e.node0, e.node1))) branchCurrents((e.node0, e.node1))
    //else compute based on V=IR
    //todo: how to handle various element types and companion models?
    else {
      e match {
        case r: Resistor => getVoltage(r) / r.resistance
        case _ => java.lang.Double.NaN
      }
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
abstract case class CompanionModel(batteries: Seq[Battery], resistors: Seq[Resistor], currentSources: Seq[CurrentSource]) {
  def getCurrent(solution: Solution): Double
}

case class Capacitor(node0: Int, node1: Int, capacitance: Double, voltage: Double, current: Double) extends Element {
  def getCompanionModel(dt: Double, newNode: () => Int) = {
    //linear companion model for capacitor, using trapezoidal approximation, under thevenin model, see http://dev.hypertriton.com/edacious/trunk/doc/lec.pdf
    val midNode = newNode()
    new CompanionModel(new Battery(node0, midNode, voltage + dt * current / 2 / capacitance) :: Nil,
      new Resistor(midNode, node1, dt / 2 / capacitance) :: Nil, Nil) {
      def getCurrent(solution: Solution) = solution.getCurrent(resistors(0))
    }
  }
}

case class Inductor(node0: Int, node1: Int, inductance: Double, voltage: Double, current: Double) extends Element {
  def getCompanionModel(dt: Double, newNode: () => Int) = {
    //linear companion model for inductor, using trapezoidal approximation, under norton model, see http://dev.hypertriton.com/edacious/trunk/doc/lec.pdf
    new CompanionModel(Nil, new Resistor(node0, node1, dt / 2 / inductance) :: Nil,
      new CurrentSource(node0, node1, current + dt * voltage / 2 / inductance) :: Nil) {
      def getCurrent(solution: Solution) = solution.getCurrent(resistors(0)) //TODO: this is surely incorrect
    }
  }
}

case class InitialCondition(voltage: Double, current: Double)
case class FullCircuit(batteries: Seq[Battery], resistors: Seq[Resistor], capacitors: Seq[Capacitor], inductors: Seq[Inductor]) extends AbstractCircuit {
  def stepInTime(dt: Double) = {
    val solution = solve(dt)
    new FullCircuit(batteries, resistors, for (c <- capacitors) yield {
      new Capacitor(c.node0, c.node1, c.capacitance, solution.getVoltage(c), solution.getCurrent(c))
    }, Nil)
  }

  def getInitializedCircuit = {
    val initConditions = getInitialConditions
    new FullCircuit(Battery(0, 1, 5.0) :: Nil, Resistor(1, 2, 10.0) :: Nil, for (c <- capacitors) yield {
      new Capacitor(c.node0, c.node1, c.capacitance, initConditions(c).voltage, initConditions(c).current)
    }, Nil)
  }
  //Create a circuit that has correct initial voltages and currents for capacitors and inductors
  //This is done by:
  // treating a capacitor as a R=0.0 resistor and computing the current through it
  // treating an inductor as a R=INF resistor and computing the voltage drop across it
  def getInitialConditions = {
    val b = new ArrayBuffer[Battery]
    b ++= batteries
    val r = new ArrayBuffer[Resistor]
    r ++= resistors
    val cs = new ArrayBuffer[CurrentSource]

    for (c <- capacitors) {
      r += new Resistor(c.node0, c.node1, 0.0)
    }
    for (i <- inductors) {
      r += new Resistor(i.node0, i.node1, 1E14) //todo: could make base model handle Infinity properly, via maths or via circuit architecture remapping
    }
    val circuit = new Circuit(b, r)
    val solution = circuit.solve
    val capacitorMap = new HashMap[Capacitor, InitialCondition]
    for (c <- capacitors) {
      capacitorMap += c -> new InitialCondition(0, circuit.getCurrent(c.node0, c.node1)) //TODO: this repeats solve in each iteration
    }
    //todo: same for inductors
    capacitorMap
  }

  def getCompanionModel(dt: Double) = {
    val b = new ArrayBuffer[Battery]
    b ++= batteries
    val r = new ArrayBuffer[Resistor]
    r ++= resistors
    val cs = new ArrayBuffer[CurrentSource]

    val usedIndices = new ArrayBuffer[Int]

    val capacitorMap = new HashMap[Capacitor, CompanionModel]
    for (c <- capacitors) {
      val cm = c.getCompanionModel(dt, () => getFreshIndex(usedIndices))
      capacitorMap += c -> cm
      for (battery <- cm.batteries) b += battery
      for (resistor <- cm.resistors) r += resistor
      for (currentSource <- cm.currentSources) cs += currentSource
    }
    new BigCompanionModel(new Circuit(b, r), capacitorMap)
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

  def solve(dt: Double) = {
    val companionModel = getCompanionModel(dt)
    val solution = companionModel.circuit.solve
    new CompanionSolution(this, companionModel, solution)
  }
}

class BigCompanionModel(val circuit: Circuit, val capacitorMap: HashMap[Capacitor, CompanionModel]) {
  def getCurrent(c: Capacitor, solution: Solution) = capacitorMap(c).getCurrent(solution)
}

class CompanionSolution(fullCircuit: FullCircuit, companionModel: BigCompanionModel, solution: Solution)
        extends Solution(solution.nodeVoltages, solution.branchCurrents) { //todo: fix this, shouldn't be able to access these values...?
  override def getVoltage(e: Element): Double = {
    e match {
      case c: Capacitor => super.getVoltage(e) //this should work because original node indices are same
      case _ => super.getVoltage(e)
    }
  }

  override def getCurrent(e: Element): Double = {
    e match {
      case c: Capacitor => companionModel.getCurrent(c, solution);
      case _ => super.getCurrent(e)
    }
  }

  //todo: fix this
  override def approxEquals(s: Solution, delta: Double) = super.approxEquals(s, delta)
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

case class Circuit(batteries: Seq[Battery], resistors: Seq[Resistor], currentSources: Seq[CurrentSource]) extends AbstractCircuit {
  def this(batteries: Seq[Battery], resistors: Seq[Resistor]) = this (batteries, resistors, Nil);

  def getElements = {
    val elements = new ArrayBuffer[Element]
    elements ++= batteries
    elements ++= resistors
    elements
  }

  def getNodeCount = getNodeSet.size

  def getCurrentCount = batteries.length + resistors.filter(_.resistance == 0).size

  def getNumVars = getNodeCount + getCurrentCount

  case class Term(coefficient: Double, variable: Unknown) {
    def toTermString = {
      val prefix = if (coefficient == 1) "" else if (coefficient == -1) "-" else coefficient + "*"
      prefix + variable.toTermName
    }
  }

  def getCurrent(node0: Int, node1: Int) = {
    val solution = solve
    //if the solution has a direct answer for this query, use it
    if (solution.branchCurrents.contains((node0, node1))) {
      solution.branchCurrents((node0, node1))
    }
    //otherwise, compute it from known values
    else {
      println("current not found")
      0.0 //TODO compute voltage
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

  //with a positive sign
  //Todo: does this get the signs right in all cases?
  def getIncomingCurrentTerms(node: Int) = {
    val nodeTerms = new ArrayBuffer[Term]
    for (b <- batteries if b.node1 == node)
      nodeTerms += Term(1, UnknownCurrent(b.node0, b.node1))
    for (r <- resistors; if r.node1 == node; if r.resistance == 0) //Treat resistors with R=0 as having unknown current and v1=v2
      nodeTerms += Term(1, UnknownCurrent(r.node0, r.node1))
    for (r <- resistors; if r.node1 == node; if r.resistance != 0) {
      nodeTerms += Term(-1 / r.resistance, UnknownVoltage(r.node1))
      nodeTerms += Term(1 / r.resistance, UnknownVoltage(r.node0))
    }
    nodeTerms
  }

  def getRHS(node: Int) = {
    var sum = 0.0
    for (c <- currentSources if c.node1 == node) sum = sum + c.current //current is entering the node
    for (c <- currentSources if c.node0 == node) sum = sum - c.current //current is going away
    sum
  }

  //outgoing currents are negative so that incoming + outgoing = 0
  def getOutgoingCurrentTerms(node: Int) = {
    val nodeTerms = new ArrayBuffer[Term]
    for (b <- batteries if b.node0 == node)
      nodeTerms += Term(-1, UnknownCurrent(b.node0, b.node1))
    for (r <- resistors; if r.node0 == node; if r.resistance == 0) //Treat resistors with R=0 as having unknown current and v1=v2
      nodeTerms += Term(-1, UnknownCurrent(r.node0, r.node1))
    for (r <- resistors; if r.node0 == node; if r.resistance != 0) {
      nodeTerms += Term(1 / r.resistance, UnknownVoltage(r.node1))
      nodeTerms += Term(-1 / r.resistance, UnknownVoltage(r.node0))
    }
    nodeTerms
  }

  def getCurrentConservationTerms(node: Int) = {
    val nodeTerms = new ArrayBuffer[Term]
    nodeTerms ++= getIncomingCurrentTerms(node)
    nodeTerms ++= getOutgoingCurrentTerms(node)
    nodeTerms
  }

  def getEquations = {
    val list = new ArrayBuffer[Equation]
    //    println("nodeset=" + getNodeSet)

    //reference node has a voltage of 0.0
    //todo: should have one zero node per strong component
    list += new Equation(0, Term(1, UnknownVoltage(getNodeSet.toSeq(0))))

    //for each node, charge is conserved
    for (node <- getNodeSet) list += new Equation(getRHS(node), getCurrentConservationTerms(node): _*) //see p. 155 scala book

    //for each battery, voltage drop is given
    for (battery <- batteries) list += new Equation(battery.voltage, Term(-1, UnknownVoltage(battery.node0)), Term(1, UnknownVoltage(battery.node1)))

    //if resistor has no resistance, node0 and node1 should have same voltage
    for (resistor <- resistors if resistor.resistance == 0) list += new Equation(0, Term(1, UnknownVoltage(resistor.node0)), Term(-1, UnknownVoltage(resistor.node1)))

    list
  }

  def getUnknownVoltages = for (node <- getNodeSet) yield UnknownVoltage(node)

  def getUnknownCurrents = {
    val unknowns = new ArrayBuffer[UnknownCurrent]
    for (battery <- batteries) unknowns += UnknownCurrent(battery.node0, battery.node1)

    //Treat resistors with R=0 as having unknown current and v1=v2
    for (resistor <- resistors if resistor.resistance == 0) unknowns += UnknownCurrent(resistor.node0, resistor.node1)
    unknowns
  }

  def getUnknowns = { //todo: probably a way to do this in one line
    val list = new ArrayBuffer[Unknown]
    list ++= getUnknownVoltages
    list ++= getUnknownCurrents
    list
  }

  def solve = {
    var equations = getEquations

    val A = new Matrix(equations.size, getNumVars)
    val z = new Matrix(equations.size, 1)
    for (i <- 0 until equations.size) equations(i).stamp(i, A, z, getUnknowns.indexOf(_)) //todo: how to handle indexing reverse voltages?  Perhaps just require that all voltages are forward (from node0 to node1)
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
    val circuit = new Circuit(Nil, Resistor(1, 0, 4.0) :: Nil, CurrentSource(0, 1, 10.0) :: Nil)
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 10.0 * 4.0), Map())
    println("equations=" + circuit.getEquations.mkString("\n"))
    println("desired=" + desiredSolution)
    println("actual=" + circuit.solve)
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
}

object TestRCCircuit {
  def main(args: Array[String]) {
    val circuit = new FullCircuit(Battery(0, 1, 5.0) :: Nil, Resistor(1, 2, 10.0) :: Nil, Capacitor(2, 0, 1.0E-2, 0.0, 0.0) :: Nil, Nil)
    val inited = circuit.getInitializedCircuit
    val v0 = -5 //todo: make sure in sync with inited circuit
    println("inited=" + inited)

    val dt = 1E-4
    var dynamicCircuit = inited
    println("time\tcurrent\tvoltage\tdesiredVoltage\terror")
    for (i <- 0 until 10000) {
      val t = i * dt
      val solution = dynamicCircuit.solve(dt)
      val current = solution.getCurrent(Battery(0, 1, 5.0))
      val voltage = solution.getVoltage(Resistor(1, 2, 10.0))
      val desiredVoltage = v0 * exp(-t / 10.0 / 1.0E-2)
      val error = voltage - desiredVoltage
      println(t + "\t" + current + "\t" + voltage + "\t" + desiredVoltage + "\t" + error)
      dynamicCircuit = dynamicCircuit.stepInTime(dt)
    }
  }
}

class Tester extends FunSuite {
  test("battery resistor circuit should have correct voltages and currents for a simple circuit") {
    val circuit = new Circuit(Array(Battery(0, 1, 4.0)), Array(Resistor(1, 0, 4.0)))
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0), Map((0, 1) -> 1.0))
    assert(circuit.solve.approxEquals(desiredSolution, 1E-6))
  }
  test("current source should provide current") {
    val circuit = new Circuit(Nil, Resistor(1, 0, 4.0) :: Nil, CurrentSource(0, 1, 10.0) :: Nil)
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 10.0 * 4.0), Map())
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
}