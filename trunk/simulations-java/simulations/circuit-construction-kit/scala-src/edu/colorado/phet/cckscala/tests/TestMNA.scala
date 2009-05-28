package edu.colorado.phet.cckscala.tests


import collection.mutable.{HashSet, ArrayBuffer}
import Jama.Matrix
import util.parsing.combinator.JavaTokenParsers

object TestMNA {
  trait Element {
    def node0: Int

    def node1: Int
  }
  case class Battery(node0: Int, node1: Int, voltage: Double) extends Element
  case class Resistor(node0: Int, node1: Int, resistance: Double) extends Element
  class NodeVoltage {}
  class BranchCurrent {}
  case class Solution(nodeVoltages: Seq[NodeVoltage], branchCurrents: Seq[BranchCurrent])
  case class NetList(batteries: Seq[Battery], resistors: Seq[Resistor]) {
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
    def getNodeAssignments(node: Int) = {
      val nodeAssignments = new ArrayBuffer[Assignment]
      for (b <- batteries if b.node0 == node) nodeAssignments += new Assignment(1, new UnknownCurrent(b.node0, b.node1))
      for (b <- batteries if b.node1 == node) nodeAssignments += new Assignment(-1, new UnknownCurrent(b.node0, b.node1))
      for (r <- resistors if r.node0 == node) {
        nodeAssignments += new Assignment(1 / r.resistance, new UnknownVoltage(r.node0))
        nodeAssignments += new Assignment(1 / r.resistance, new UnknownVoltage(r.node1))
      }
      for (r <- resistors if r.node1 == node) {
        nodeAssignments += new Assignment(-1 / r.resistance, new UnknownVoltage(r.node0))
        nodeAssignments += new Assignment(-1 / r.resistance, new UnknownVoltage(r.node1))
      }
      nodeAssignments
    }

    def getEquations = {
      val list = new ArrayBuffer[Equation]
      println("nodeset=" + getNodeSet)
      //reference node
      list += new Equation(0, new Assignment(1, new UnknownVoltage(getNodeSet.toSeq(0))))
      //for each node, charge is conserved
      for (node <- getNodeSet) list += new Equation(0, getNodeAssignments(node): _*) //see p. 155 scala book
      //for each battery, voltage drop is given
      for (b <- batteries) list += new Equation(b.voltage, new Assignment(-1, new UnknownVoltage(b.node0)), new Assignment(1, new UnknownVoltage(b.node1)))
      list
    }

    def getUnknowns = {
      val list = new ArrayBuffer[Unknown]
      val ns = getNodeSet
      for (n <- ns) list += new UnknownVoltage(n)
      for (b <- batteries) list += new UnknownCurrent(b.node0, b.node1)
      list
    }

    def solve: Solution = {
      var equations = getEquations
      var unknowns = getUnknowns
      println(equations.mkString("\n"))
      val numVars = getNumVars
      val A = new Matrix(equations.size, getNumVars)
      val z = new Matrix(equations.size, 1)
      for (i <- 0 until equations.size) equations(i).stamp(i, A, z, unknowns.indexOf(_)) //todo: how to handle indexing reverse voltages
      A.print(4, 2)
      z.print(4, 2)
      val x = A.solve(z)
      print("unknowns=\n" + unknowns.mkString("\n"))
      x.print(4, 2)
      new Solution(new ArrayBuffer[NodeVoltage], new ArrayBuffer[BranchCurrent])
    }
  }
  def main(args: Array[String]) {
    println("testing")
    val netList = new NetList(Array(Battery(0, 1, 4.0)), Array(Resistor(1, 0, 4.0)))
    val solution = netList.solve
    println(solution)
  }
}