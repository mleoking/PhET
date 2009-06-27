package edu.colorado.phet.cckscala.tests

import collection.mutable.{HashMap, HashSet, ArrayBuffer}
import Jama.Matrix
import org.scalatest.FunSuite
import java.lang.Math._

import util.parsing.combinator.JavaTokenParsers
import util.Sorting

/**
 * Conventions:
 * Current is 'conventional current': in a battery positive current flows from the higher (+) potential
 */
trait ISolution {
  def getNodeVoltage(node: Int): Double

  def getCurrent(element: Element): Double

  def getVoltageDifference(node0: Int, node1: Int) = getNodeVoltage(node1) - getNodeVoltage(node0)
}
//sparse solution containing only the solved unknowns in MNA
case class Solution(private val nodeVoltages: collection.Map[Int, Double], private val branchCurrents: collection.Map[Element, Double]) extends ISolution {
  def getNodeVoltage(node: Int) = nodeVoltages(node)

  def approxEquals(s: Solution): Boolean = approxEquals(s, 1E-6)

  def approxEquals(s: Solution, delta: Double): Boolean = {
    if (nodeVoltages.keySet != s.nodeVoltages.keySet || branchCurrents.keySet != s.branchCurrents.keySet)
      false
    else {
      val sameVoltages = nodeVoltages.keySet.foldLeft(true)((b: Boolean, a: Int) => {b && Math.abs(nodeVoltages(a) - s.nodeVoltages(a)) < delta})
      val sameCurrents = branchCurrents.keySet.foldLeft(true)((b: Boolean, a: Element) => {b && Math.abs(branchCurrents(a) - s.branchCurrents(a)) < delta})
      sameVoltages && sameCurrents
    }
  }

  def getVoltage(e: Element) = nodeVoltages(e.node1) - nodeVoltages(e.node0)

  def getCurrent(e: Element) = {
    //if it was a battery or resistor (of R=0), look up the answer
    if (branchCurrents.contains(e)) branchCurrents(e)
    //else compute based on V=IR
    else {
      e match {
        //current flows from high to low potential in a component (except batteries) 
        case r: Resistor => -getVoltage(r) / r.resistance
        case _ => throw new RuntimeException("Solution does not contain current for element: " + e)
      }
    }
  }
}

//Subclasses should have proper equals and hashcode for hashmapping
trait Element {
  def node0: Int

  def node1: Int

  def containsNode(n: Int) = n == node0 || n == node1

  def getOpposite(n: Int): Int = if (n == node0) node1 else if (n == node1) node0 else throw new RuntimeException("error")
}

case class Battery(node0: Int, node1: Int, voltage: Double) extends Element
case class Resistor(node0: Int, node1: Int, resistance: Double) extends Element
case class CurrentSource(node0: Int, node1: Int, current: Double) extends Element

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

  def getElements: Seq[Element] = batteries.toList ::: resistors.toList ::: currentSources.toList

  def getNodeCount = getNodeSet.size

  def getCurrentCount = batteries.length + resistors.filter(_.resistance == 0).size

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
  case class UnknownCurrent(element: Element) extends Unknown {
    def toTermName = "I" + element.node0 + "_" + element.node1
  }
  case class UnknownVoltage(node: Int) extends Unknown {
    def toTermName = "V" + node
  }


  def getRHS(node: Int) = {
    var sum = 0.0
    for (c <- currentSources if c.node1 == node) sum = sum - c.current //current is entering the node//TODO: these signs seem backwards, shouldn't incoming current add?
    for (c <- currentSources if c.node0 == node) sum = sum + c.current //current is going away
    sum
  }
  //Todo: does this get the signs right in all cases?
  //TODO: maybe signs here should depend on component orientation?

  //incoming current is negative, outgoing is positive
  def getIncomingCurrentTerms(node: Int) = {
    val nodeTerms = new ArrayBuffer[Term]
    for (b <- batteries if b.node1 == node)
      nodeTerms += Term(-1, UnknownCurrent(b))
    for (r <- resistors; if r.node1 == node; if r.resistance == 0) //Treat resistors with R=0 as having unknown current and v1=v2
      nodeTerms += Term(-1, UnknownCurrent(r))
    for (r <- resistors; if r.node1 == node; if r.resistance != 0) {
      nodeTerms += Term(1 / r.resistance, UnknownVoltage(r.node1))
      nodeTerms += Term(-1 / r.resistance, UnknownVoltage(r.node0))
    }
    nodeTerms
  }
  //outgoing currents are negative so that incoming + outgoing = 0
  def getOutgoingCurrentTerms(node: Int) = {
    val nodeTerms = new ArrayBuffer[Term]
    for (b <- batteries if b.node0 == node)
      nodeTerms += Term(1, UnknownCurrent(b))
    for (r <- resistors; if r.node0 == node; if r.resistance == 0) //Treat resistors with R=0 as having unknown current and v1=v2
      nodeTerms += Term(1, UnknownCurrent(r))
    for (r <- resistors; if r.node0 == node; if r.resistance != 0) {
      nodeTerms += Term(-1 / r.resistance, UnknownVoltage(r.node1))
      nodeTerms += Term(1 / r.resistance, UnknownVoltage(r.node0))
    }
    nodeTerms
  }

  def getCurrentConservationTerms(node: Int) = {
    val nodeTerms = new ArrayBuffer[Term]
    nodeTerms ++= getIncomingCurrentTerms(node)
    nodeTerms ++= getOutgoingCurrentTerms(node)
    nodeTerms
  }

  //obtain one node for each connected component to have the reference voltage of 0.0
  def getReferenceNodes = {
    val nodeSet: HashSet[Int] = getNodeSet
    val remaining = new HashSet[Int]
    remaining ++= nodeSet
    val referenceNodes = new HashSet[Int]
    while (remaining.size > 0) {
      val sorted: Array[Int] = Sorting.stableSort(remaining.toArray)
      referenceNodes += sorted(0)
      val connected = getConnectedNodes(sorted(0))
      remaining --= connected
    }
    referenceNodes
  }

  def getConnectedNodes(node: Int): HashSet[Int] = {
    val visited = new HashSet[Int]
    val toVisit = new HashSet[Int]
    toVisit += node
    getConnectedNodes(visited, toVisit)
    visited
  }

  private def getConnectedNodes(visited: HashSet[Int], toVisit: HashSet[Int]): Unit = {
    while (toVisit.size > 0) {
      val n = toVisit.toArray(0)
      visited += n
      for (e <- getElements) {
        if (e.containsNode(n) && !visited.contains(e.getOpposite(n))) {
          toVisit += e.getOpposite(n)
        }
      }
      toVisit -= n
    }
  }

  def getEquations = {
    val list = new ArrayBuffer[Equation]
    //    println("nodeset=" + getNodeSet)

    //reference node in each connected component has a voltage of 0.0
    for (n <- getReferenceNodes) list += new Equation(0, Term(1, UnknownVoltage(n)))

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
    for (battery <- batteries) unknowns += UnknownCurrent(battery)

    //Treat resistors with R=0 as having unknown current and v1=v2
    for (resistor <- resistors if resistor.resistance == 0) unknowns += UnknownCurrent(resistor)
    unknowns
  }

  def getUnknowns = getUnknownCurrents.toList ::: getUnknownVoltages.toList

  def solve = {
    var equations = getEquations

    val A = new Matrix(equations.size, getNumVars)
    val z = new Matrix(equations.size, 1)
    for (i <- 0 until equations.size) equations(i).stamp(i, A, z, getUnknowns.indexOf(_))
    val x = A.solve(z)

    val voltageMap = new HashMap[Int, Double]
    for (nodeVoltage <- getUnknownVoltages) voltageMap(nodeVoltage.node) = x.get(getUnknowns.indexOf(nodeVoltage), 0)

    val currentMap = new HashMap[Element, Double]
    for (currentVar <- getUnknownCurrents) currentMap(currentVar.element) = x.get(getUnknowns.indexOf(currentVar), 0)

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
        val battery = Battery(0, 1, 4.0)
    val resistor = Resistor(1, 2, 4.0)
    val resistor2 = Resistor(2, 0, 0.0)
    val circuit = new Circuit(battery :: Nil, resistor :: resistor2:: Nil)
    val desiredSolution = new Solution(Map(0 -> 0.0, 1 -> 4.0, 2->0.0), Map(battery -> 1.0,resistor2->1.0))
    circuit.debug=true
    println("circuit.solve="+circuit.solve)
    println("desired solution="+desiredSolution)
    circuit.debug=false
    assert(circuit.solve.approxEquals(desiredSolution))
  }
}