package edu.colorado.phet.cckscala.tests


import collection.mutable.{ArrayBuffer, HashMap}
import java.util.ArrayList

abstract case class CompanionModel(batteries: Seq[Battery], resistors: Seq[Resistor], currentSources: Seq[CurrentSource]) {
  def getCurrent(solution: Solution): Double

  def getVoltage(solution: Solution): Double
}

trait HasCompanionModel {
  def getCompanionModel(dt: Double, newNode: () => Int): CompanionModel
}
case class Capacitor(node0: Int, node1: Int, capacitance: Double, voltage: Double, current: Double) extends Element with HasCompanionModel {
  def getCompanionModel(dt: Double, newNode: () => Int) = {
    //linear companion model for capacitor, using trapezoidal approximation, under thevenin model, see http://dev.hypertriton.com/edacious/trunk/doc/lec.pdf
    //and p.23 pillage
    val midNode = newNode()
    new CompanionModel(new Battery(node0, midNode, voltage + dt * current / 2 / capacitance) :: Nil,
      new Resistor(midNode, node1, dt / 2 / capacitance) :: Nil, Nil) {
      def getCurrent(solution: Solution) = -solution.getCurrent(batteries(0))//todo: why is minus sign here?

      def getVoltage(solution: Solution) = voltage+dt/2/capacitance*(current+getCurrent(solution))//seems to have same behavior as line below
//      def getVoltage(solution: Solution) = solution.getNodeVoltage(node1)-solution.getNodeVoltage(node0)
    }
  }
}

case class Inductor(node0: Int, node1: Int, inductance: Double, voltage: Double, current: Double) extends Element with HasCompanionModel {
  def getCompanionModel(dt: Double, newNode: () => Int) = {
    //Thevenin, Pillage p.23.  Pillage says this is the model used in Spice
    val midNode = newNode()
    new CompanionModel(Battery(node0, midNode, voltage + 2 * inductance * current / dt) :: Nil,
      new Resistor(midNode, node1, 2 * inductance / dt) :: Nil, Nil) {
      def getCurrent(solution: Solution) = solution.getCurrent(batteries(0))

      def getVoltage(solution: Solution) = (getCurrent(solution)-current)*2*inductance/dt-voltage
    }
  }
}
object JavaUtil{
    def toSeq[T](javalist:ArrayList[T]):Seq[T]={
    val arrayBuffer=new ArrayBuffer[T]
    for (i <- 0 until javalist.size)
      arrayBuffer+=javalist.get(i)
    arrayBuffer
  }
}
case class InitialCondition(voltage: Double, current: Double)
case class FullCircuit(batteries: Seq[Battery], resistors: Seq[Resistor], capacitors: Seq[Capacitor], inductors: Seq[Inductor]) extends AbstractCircuit {
  //This auxiliary constructor facilitates communication with java
  def this(b:ArrayList[Battery],r:ArrayList[Resistor],c:ArrayList[Capacitor],i:ArrayList[Inductor])=this(JavaUtil.toSeq(b),JavaUtil.toSeq(r),JavaUtil.toSeq(c),JavaUtil.toSeq(i))
  def stepInTime(dt: Double) = {
    val solution = solve(dt)
    new FullCircuit(batteries, resistors,
      for (c <- capacitors) yield {
        new Capacitor(c.node0, c.node1, c.capacitance, solution.getVoltage(c), solution.getCurrent(c))
      },
      for (i <- inductors) yield {
        new Inductor(i.node0, i.node1, i.inductance, solution.getVoltage(i), solution.getCurrent(i))
      })
  }

  def getInitializedCircuit = {
    val initConditions: InitialConditionSet = getInitialConditions
    new FullCircuit(batteries, resistors,
      for (c <- capacitors) yield {
        new Capacitor(c.node0, c.node1, c.capacitance, initConditions.capacitorMap(c).voltage, initConditions.capacitorMap(c).current)
      }, for (i <- inductors) yield {
        new Inductor(i.node0, i.node1, i.inductance, initConditions.inductorMap(i).voltage, initConditions.inductorMap(i).current)
      })
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

    val capToRes = new HashMap[Capacitor, Resistor]
    for (c <- capacitors) {
      val resistor = new Resistor(c.node0, c.node1, 0.0)
      r += resistor
      capToRes += c -> resistor
    }

    val indToRes = new HashMap[Inductor, Resistor]
    for (i <- inductors) {
      val resistor = new Resistor(i.node0, i.node1, 1E14)
      r += resistor //todo: could make base model handle Infinity properly, via maths or via circuit architecture remapping
      indToRes += i -> resistor
    }
    val circuit = new Circuit(b, r)
    val solution = circuit.solve

    val capacitorMap = new HashMap[Capacitor, InitialCondition]
    for (c <- capacitors) capacitorMap += c -> new InitialCondition(0, solution.getCurrent(capToRes(c)))

    val inductorMap = new HashMap[Inductor, InitialCondition]
    for (i <- inductors) inductorMap += i -> new InitialCondition(solution.getVoltage(indToRes(i)), 0.0)

    new InitialConditionSet(capacitorMap, inductorMap)
  }
  case class InitialConditionSet(capacitorMap: HashMap[Capacitor, InitialCondition], inductorMap: HashMap[Inductor, InitialCondition])

  def getCompanionModel(dt: Double) = {
    val b = new ArrayBuffer[Battery]
    b ++= batteries
    val r = new ArrayBuffer[Resistor]
    r ++= resistors
    val cs = new ArrayBuffer[CurrentSource]

    val usedIndices = new ArrayBuffer[Int]

    val companionMap = new HashMap[HasCompanionModel, CompanionModel]
    val sourceElements: Seq[HasCompanionModel] = capacitors.toList ::: inductors.toList
    for (c <- sourceElements) {
      val cm = c.getCompanionModel(dt, () => getFreshIndex(usedIndices))
      companionMap += c -> cm
      for (battery <- cm.batteries) b += battery
      for (resistor <- cm.resistors) r += resistor
      for (currentSource <- cm.currentSources) cs += currentSource
    }
    new CompanionCircuit(new Circuit(b, r, cs), companionMap)
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

case class CompanionCircuit(val circuit: Circuit, val elementMap: HashMap[HasCompanionModel, CompanionModel]) {
  def getCurrent(c: HasCompanionModel, solution: Solution) = elementMap(c).getCurrent(solution)

  def getVoltage(c: HasCompanionModel, solution: Solution) = elementMap(c).getVoltage(solution)
}

class CompanionSolution(fullCircuit: FullCircuit, companionModel: CompanionCircuit, solution: Solution) extends ISolution {
  def getNodeVoltage(node: Int) = solution.getNodeVoltage(node)

  def getVoltage(e: Element) = {
    e match {
      case c: HasCompanionModel => companionModel.getVoltage(c, solution)
      case _ => solution.getVoltage(e)
    }
  }

  def getCurrent(e: Element) = {
    e match {
      case c: HasCompanionModel => companionModel.getCurrent(c, solution)
      case _ => solution.getCurrent(e)
    }
  }
}
