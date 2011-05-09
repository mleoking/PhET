package edu.colorado.phet.cckscala.tests

import collection.mutable.{HashSet, HashMap, ArrayBuffer}

class Capacitor(val node0: Int, val node1: Int, val capacitance: Double) extends Element
class Inductor(val node0: Int, val node1: Int, val inductance: Double) extends Element
class ResistiveBattery(val node0: Int, val node1: Int, val voltage: Double, val resistance: Double) extends Element //This models a battery with a resistance in series

object MathUtil {
  def euclideanDistance(x: Seq[Double], y: Seq[Double]) = {
    if (x.length != y.length) throw new RuntimeException("Vector length mismatch")
    val sqDiffs = for (i <- 0 until x.length) yield (x(i) - y(i)) * (x(i) - y(i))
    val sum = sqDiffs.foldLeft(0.0)(_ + _)
    Math.sqrt(sum)
  }
}
case class CState(voltage: Double, current: Double)

/**This is a rewrite of companion mapping to make it simpler to construct and inspect companion models.*/
case class DynamicCircuit(batteries: Seq[Battery], resistors: Seq[Resistor], currents: Seq[CurrentSource], resistiveBatteries: Seq[ResistiveBattery], capacitors: Seq[(Capacitor, CState)], inductors: Seq[(Inductor, CState)]) {

  //Solving the companion model is the same as propagating forward in time by dt.
  def solvePropagate(dt: Double) = {
    val (mnaCircuit, currentCompanions) = toMNACircuit(dt)
    //    println("called solve propagate, companions = "+currentCompanions)
    new DynamicCircuitSolution(this, mnaCircuit.solve, currentCompanions)
  }

  case class DynamicState(circuit: DynamicCircuit, solution: DynamicCircuitSolution) {
    def update(dt: Double) = {
      val solution = circuit.solvePropagate(dt)
      val newCircuit = circuit.updateCircuit(solution)
      new DynamicState(newCircuit, solution)
    }
  }

  def solveWithSubdivisions(dt: Double) = {
    val steppable = new Steppable[DynamicState] {
      def update(a: DynamicState, dt: Double) = a.update(dt)

      //TODO: generalize distance criterion, will be simpler if solutions are incorporated
      def distance(a: DynamicState, b: DynamicState) = {
        //for now, just compare current through the capacitors
        val aCurrents = for (c <- a.circuit.capacitors) yield c._2.current
        val bCurrents = for (c <- b.circuit.capacitors) yield c._2.current //todo: read from solution object
        val euclideanDistance = MathUtil.euclideanDistance(aCurrents, bCurrents)
        euclideanDistance
      }
    }
    new TimestepSubdivisions(1E-7).stepInTime(new DynamicState(this, null), steppable, dt)
  }

  def updateWithSubdivisions(dt: Double) = solveWithSubdivisions(dt).circuit

  def solveItWithSubdivisions(dt: Double) = solveWithSubdivisions(dt).solution

  def update(dt: Double) = updateCircuit(solvePropagate(dt))

  //Applies the specified solution to the circuit.
  def updateCircuit(solution: DynamicCircuitSolution) = {
    val updatedCapacitors = for (cap <- capacitors; c = cap._1) yield (c, new CState(solution.getNodeVoltage(c.node1) - solution.getNodeVoltage(c.node0), solution.getCurrent(c)))
    //todo: update inductors
    new DynamicCircuit(batteries, resistors, currents, resistiveBatteries, updatedCapacitors, inductors)
  }

  //why not give every component a companion in the MNACircuit?
  def toMNACircuit(dt: Double) = {
    val companionBatteries = new ArrayBuffer[Battery]
    val companionResistors = new ArrayBuffer[Resistor]
    val companionCurrents = new ArrayBuffer[CurrentSource]
    val currentCompanions = new HashMap[Element, Solution => Double]

    val usedNodes = new HashSet[Int] {
      val allElements: Seq[Element] = batteries.toList ++ resistors.toList ++ currents.toList ++ resistiveBatteries.toList ++ (for (c <- capacitors) yield c._1).toList ++ (for (i <- inductors) yield i._1).toList
      for (b <- allElements) {
        this += b.node0
        this += b.node1
      }
    }

    def max(m: List[Int]) = m.sortWith(_ < _).reverse.head

    //each resistive battery is a resistor in series with a battery
    for (b <- resistiveBatteries) {
      val newNode = max(usedNodes.toList) + 1
      usedNodes += newNode

      val idealBattery =new Battery(b.node0,newNode,b.voltage)
      val resistor = new Resistor(newNode,b.node1,b.resistance)
      companionBatteries += idealBattery
      companionResistors += resistor
      //we need to be able to get the current for this component
      currentCompanions(b) = (s: Solution) => s.getCurrent(idealBattery) //in series, so current is same through both companion components
    }

    //add companion models for capacitor

    //TRAPEZOIDAL
    //        double vc = state.v + dt / 2 / c * state.i;
    //        double rc = dt / 2 / c;

    //BACKWARD EULER
    //        double vc = state.v;
    //        double rc = dt / c;
    for (c <- capacitors) {
      val capacitor = c._1
      val cstate = c._2
      //in series
      val newNode = max(usedNodes.toList) + 1
      usedNodes += newNode

      val companionResistance = dt / 2.0 / capacitor.capacitance
      val companionVoltage = cstate.voltage - companionResistance * cstate.current //TODO: explain the difference between this sign and the one in TestTheveninCapacitorRC
      //      println("companion resistance = "+companionResistance+", companion voltage = "+companionVoltage)

      val battery = new Battery(capacitor.node0, newNode, companionVoltage)
      val resistor = new Resistor(newNode, capacitor.node1, companionResistance)
      companionBatteries += battery
      companionResistors += resistor
      //we need to be able to get the current for this component

      currentCompanions(capacitor) = (s: Solution) => s.getCurrent(battery) //in series, so current is same through both companion components
    }
    //        println("currentCompanions = " + currentCompanions)
    //    for (i <- inductors) {
    //      mnaBatteries += new Battery
    //      mnaCurrents += new CurrentSource
    //    }
    (new Circuit(batteries ++ companionBatteries, resistors ++ companionResistors, currents ++ companionCurrents), currentCompanions)
  }
}

case class DynamicCircuitSolution(circuit: DynamicCircuit, mnaSolution: Solution, currentCompanions: HashMap[Element, Solution => Double]) {
  def getNodeVoltage(node: Int) = mnaSolution.getNodeVoltage(node)

  def getCurrent(element: Element) = {
    if (currentCompanions.contains(element))
      currentCompanions(element)(mnaSolution)
    else
      mnaSolution.getCurrent(element)
  }
}

object TestDynamicCircuit {
  def main(args: Array[String]) {
    val voltage = 9.0
    //    val resistance = 1E-6
    val resistance = 1
    val c = new Capacitor(2, 0, 0.1)
    val battery = new Battery(0, 1, voltage)
    var circuit = new DynamicCircuit(battery :: Nil, new Resistor(1, 2, resistance) :: Nil, Nil, Nil, (c, new CState(0.0, voltage / resistance)) :: Nil, Nil)
    //    var circuit = new CompanionCircuit(battery :: Nil, new Resistor(1, 0, resistance) :: Nil, Nil, Nil, Nil)
    println("current through capacitor")
    for (i <- 0 until 10) {
      val solution = circuit.solveItWithSubdivisions(0.03)
      val startTime = System.currentTimeMillis
      circuit = circuit.updateWithSubdivisions(0.03)
      val endTime = System.currentTimeMillis
      println("time = " + (endTime-startTime))
      //      println("Circuit: "+circuit)
      println("companions = " + solution.currentCompanions)
      println(circuit.capacitors(0)._2.current + "\t" + solution.getCurrent(c))
    }
  }
}