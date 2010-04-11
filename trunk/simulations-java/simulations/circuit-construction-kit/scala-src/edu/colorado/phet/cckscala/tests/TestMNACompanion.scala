package edu.colorado.phet.cckscala.tests

import collection.mutable.{HashSet, HashMap, ArrayBuffer}

object MathUtil {
  def euclideanDistance(x: Seq[Double], y: Seq[Double]) = {
    if (x.length != y.length) throw new RuntimeException("Vector length mismatch")
    val sqDiffs = for (i <- 0 until x.length) yield (x(i) - y(i)) * (x(i) - y(i))
    val sum = sqDiffs.foldLeft(0.0)(_ + _)
    Math.sqrt(sum)
  }
}

/**This is a rewrite of companion mapping to make it simpler to construct and inspect companion models.*/
case class DynamicCircuit(batteries: Seq[Battery], resistors: Seq[Resistor], currents: Seq[CurrentSource], capacitors: Seq[Capacitor], inductors: Seq[Inductor]) {

  //Solving the companion model is the same as propagating forward in time by dt.
  def solvePropagate(dt: Double) = {
    val (mnaCircuit, currentCompanions) = toMNACircuit(dt)
    new DynamicCircuitSolution(this, mnaCircuit.solve, currentCompanions)
  }

  case class DynamicState(circuit: DynamicCircuit, solution: DynamicCircuitSolution) {
    def update(dt: Double) = {
      val solution = circuit.solvePropagate(dt)
      val newCircuit = circuit.updateCircuit(solution)
      new DynamicState(newCircuit, solution)
    }
  }

  def solveWithSubdivisions(dt:Double) = {
    val steppable = new Steppable[DynamicState] {
      def update(a: DynamicState, dt: Double) = a.update(dt)

      //TODO: generalize distance criterion, will be simpler if solutions are incorporated
      def distance(a: DynamicState, b: DynamicState) = {
        //for now, just compare current through the capacitors
        val aCurrents = for (c <- a.circuit.capacitors) yield c.current
        val bCurrents = for (c <- b.circuit.capacitors) yield c.current //todo: read from solution object
        val euclideanDistance = MathUtil.euclideanDistance(aCurrents, bCurrents)
        euclideanDistance
      }
    }
    new TimestepSubdivisions(1E-7).update(new DynamicState(this, null), steppable, dt)
  }

  def updateWithSubdivisions(dt: Double) = solveWithSubdivisions(dt).circuit

  def update(dt: Double) = updateCircuit(solvePropagate(dt))

  //Applies the specified solution to the circuit.
  def updateCircuit(solution: DynamicCircuitSolution) = {
    val updatedCapacitors = for (c <- capacitors) yield new Capacitor(c.node0, c.node1, c.capacitance, solution.getNodeVoltage(c.node1) - solution.getNodeVoltage(c.node0), solution.getCurrent(c))
    //todo: update inductors
    new DynamicCircuit(batteries, resistors, currents, updatedCapacitors, inductors)
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

      val companionResistance = dt / 2.0 / c.capacitance
      val companionVoltage = c.voltage - companionResistance * c.current//TODO: explain the difference between this sign and the one in TestTheveninCapacitorRC
//      println("companion resistance = "+companionResistance+", companion voltage = "+companionVoltage)

      val battery = new Battery(c.node0, newNode, companionVoltage)
      val resistor = new Resistor(newNode, c.node1, companionResistance)
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

case class DynamicCircuitSolution(circuit: DynamicCircuit, mnaSolution: Solution, currentCompanions: HashMap[Element, Solution => Double]) {
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
    var circuit = new DynamicCircuit(battery :: Nil, new Resistor(1, 2, resistance) :: Nil, Nil, c :: Nil, Nil)
    //    var circuit = new CompanionCircuit(battery :: Nil, new Resistor(1, 0, resistance) :: Nil, Nil, Nil, Nil)
    println("current through capacitor")
    for (i <- 0 until 10) {
      circuit = circuit.updateWithSubdivisions(0.03)
//      println("Circuit: "+circuit)
      println(circuit.capacitors(0).current)
    }
  }
}