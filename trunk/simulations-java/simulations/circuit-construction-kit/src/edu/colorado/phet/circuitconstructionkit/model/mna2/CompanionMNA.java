//package edu.colorado.phet.circuitconstructionkit.model.mna2;
//
//        class CompanionMNA{
//abstract case class CompanionModel(batteries: Seq[Battery], resistors: Seq[Resistor], currentSources: Seq[CurrentSource]) {
//  double getCurrent(Solution solution);
//
//  double getVoltage(Solution solution);
//}
//            interface NodeCreator{
//                int newNode();
//            }
//interface HasCompanionModel {
//  CompanionModel getCompanionModel(double dt,NodeCreator newNode);
//}
//case class Capacitor(node0: Int, node1: Int, capacitance: Double, voltage: Double, current: Double) extends Element with HasCompanionModel {
//  def getCompanionModel(dt: Double, newNode: () => Int) = {
//    //linear companion model for capacitor, using trapezoidal approximation, under thevenin model, see http://dev.hypertriton.com/edacious/trunk/doc/lec.pdf
//    //and p.23 pillage
//    //our signs differ from Pillage because:
//    //at T=0 across an uncharged capacitor, the capacitor should create a simulated voltage that prevents more charge
//    //from building up on the capacitor; this means a negative voltage (or a backwards battery)
//    val midNode = newNode()
//    new CompanionModel(
//      new Battery(node0, midNode, voltage - dt * current / 2 / capacitance) :: Nil,
//      new Resistor(midNode, node1, dt / 2 / capacitance) :: Nil, Nil) {
//      def getCurrent(solution: Solution) = solution.getCurrent(batteries(0))
//
//      def getVoltage(solution: Solution) = voltage - dt / 2 / capacitance * (current + getCurrent(solution))
//    }
//  }
//}
//
//case class Inductor(node0: Int, node1: Int, inductance: Double, voltage: Double, current: Double) extends Element with HasCompanionModel {
//  def getCompanionModel(dt: Double, newNode: () => Int) = {
//    //Thevenin, Pillage p.23.  Pillage says this is the model used in Spice
//    val midNode = newNode()
//    new CompanionModel(Battery(node0, midNode, voltage + 2 * inductance * current / dt) :: Nil,
//      new Resistor(midNode, node1, 2 * inductance / dt) :: Nil, Nil) {
//      def getCurrent(solution: Solution) = solution.getCurrent(batteries(0))
//
//      def getVoltage(solution: Solution) = (getCurrent(solution) - current) * 2 * inductance / dt - voltage
//    }
//  }
//}
//
////This models a battery with a resistance in series
//case class ResistiveBattery(node0: Int, node1: Int, voltage: Double, resistance: Double) extends Element with HasCompanionModel {
//  def getCompanionModel(dt: Double, newNode: () => Int) = {
//    val midNode = newNode()
//    new CompanionModel(Battery(node0, midNode, voltage) :: Nil,
//      new Resistor(midNode, node1, resistance) :: Nil, Nil) {
//      def getCurrent(solution: Solution) = solution.getCurrent(batteries(0))
//
//      def getVoltage(solution: Solution) = solution.getVoltageDifference(node0, node1)
//    }
//  }
//}
//
//case class InitialCondition(voltage: Double, current: Double)
//case class FullCircuit(batteries: Seq[ResistiveBattery], resistors: Seq[Resistor], capacitors: Seq[Capacitor], inductors: Seq[Inductor]) extends AbstractCircuit {
//
//  def stepInTime(dt: Double) = {
//    val solution = solve(dt)
//    new FullCircuit(batteries, resistors,
//      for (c <- capacitors) yield {
//        new Capacitor(c.node0, c.node1, c.capacitance, solution.getVoltage(c), solution.getCurrent(c))
//      },
//      for (i <- inductors) yield {
//        new Inductor(i.node0, i.node1, i.inductance, solution.getVoltage(i), solution.getCurrent(i))
//      })
//  }
//
//  def getInitializedCircuit = {
//    val initConditions: InitialConditionSet = getInitialConditions
//    new FullCircuit(batteries, resistors,
//      for (c <- capacitors) yield {
//        new Capacitor(c.node0, c.node1, c.capacitance, initConditions.capacitorMap(c).voltage, initConditions.capacitorMap(c).current)
//      }, for (i <- inductors) yield {
//        new Inductor(i.node0, i.node1, i.inductance, initConditions.inductorMap(i).voltage, initConditions.inductorMap(i).current)
//      })
//  }
//  //Create a circuit that has correct initial voltages and currents for capacitors and inductors
//  //This is done by:
//  // treating a capacitor as a R=0.0 resistor and computing the current through it
//  // treating an inductor as a R=INF resistor and computing the voltage drop across it
//  //Todo: finding inital bias currently ignores internal resistance in batteries
//  //Todo: Is this computation even used by CCK?  Should it be?
//  def getInitialConditions = {
//    val b = new ArrayBuffer[Battery]
//    val r = new ArrayBuffer[Resistor]
//    r ++= resistors
//    val cs = new ArrayBuffer[CurrentSource]
//
//    for (batt<-batteries) {
//      b+=new Battery(batt.node0,batt.node1,batt.voltage)//todo: account for internal resistance of battery in initial bias computation
//    }
//
//    val capToRes = new HashMap[Capacitor, Resistor]
//    for (c <- capacitors) {
//      val resistor = new Resistor(c.node0, c.node1, 0.0)
//      r += resistor
//      capToRes += c -> resistor
//    }
//
//    val indToRes = new HashMap[Inductor, Resistor]
//    for (i <- inductors) {
//      val resistor = new Resistor(i.node0, i.node1, 1E14)
//      r += resistor //todo: could make base model handle Infinity properly, via maths or via circuit architecture remapping
//      indToRes += i -> resistor
//    }
//    val circuit = new Circuit(b, r)
//    val solution = circuit.solve
//
//    val capacitorMap = new HashMap[Capacitor, InitialCondition]
//    for (c <- capacitors) capacitorMap += c -> new InitialCondition(0, solution.getCurrent(capToRes(c)))
//
//    val inductorMap = new HashMap[Inductor, InitialCondition]
//    for (i <- inductors) inductorMap += i -> new InitialCondition(solution.getVoltage(indToRes(i)), 0.0)
//
//    new InitialConditionSet(capacitorMap, inductorMap)
//  }
//  case class InitialConditionSet(capacitorMap: HashMap[Capacitor, InitialCondition], inductorMap: HashMap[Inductor, InitialCondition])
//
//  def getCompanionModel(dt: Double) = {
//    val b = new ArrayBuffer[Battery]//batteries use companion model since they have optionally have internal resistance
//    val r = new ArrayBuffer[Resistor]
//    r ++= resistors
//    val cs = new ArrayBuffer[CurrentSource]
//
//    val usedIndices = new ArrayBuffer[Int]
//
//    val companionMap = new HashMap[HasCompanionModel, CompanionModel]
//    val sourceElements: Seq[HasCompanionModel] = capacitors.toList ::: inductors.toList ::: batteries.toList
//    for (c <- sourceElements) {
//      val cm = c.getCompanionModel(dt, () => getFreshIndex(usedIndices))
//      companionMap += c -> cm
//      for (battery <- cm.batteries) b += battery
//      for (resistor <- cm.resistors) r += resistor
//      for (currentSource <- cm.currentSources) cs += currentSource
//    }
//    new CompanionCircuit(new Circuit(b, r, cs), companionMap)
//  }
//
//  //Find the first node index that is unused in the node set or used indices, and update the used indices
//  def getFreshIndex(usedIndices: ArrayBuffer[Int]) = {
//    var selected = -1
//    var testIndex = 0
//    while (selected == -1) {
//      if (!getNodeSet.contains(testIndex) && !usedIndices.contains(testIndex)) {
//        selected = testIndex
//      }
//      testIndex = testIndex + 1
//    }
//    usedIndices += selected
//    selected
//  }
//
//  def getElements: Seq[Element] = batteries.toList ::: resistors.toList ::: capacitors.toList ::: inductors.toList
//
//  def solve(dt: Double) = {
//    val companionModel = getCompanionModel(dt)
//    val solution = companionModel.circuit.solve
//    new CompanionSolution(this, companionModel, solution)
//  }
//}
//
//case class CompanionCircuit(val circuit: Circuit, val elementMap: HashMap[HasCompanionModel, CompanionModel]) {
//  def getCurrent(c: HasCompanionModel, solution: Solution) = elementMap(c).getCurrent(solution)
//
//  def getVoltage(c: HasCompanionModel, solution: Solution) = elementMap(c).getVoltage(solution)
//}
//
//class CompanionSolution(fullCircuit: FullCircuit, companionModel: CompanionCircuit, solution: Solution) extends ISolution {
//  def getNodeVoltage(node: Int) = solution.getNodeVoltage(node)
//
//  def getVoltage(e: Element) = {
//    e match {
//      case c: HasCompanionModel => companionModel.getVoltage(c, solution)
//      case _ => solution.getVoltage(e)
//    }
//  }
//
//  def getCurrent(e: Element) = {
//    e match {
//      case c: HasCompanionModel => companionModel.getCurrent(c, solution)
//      case _ => solution.getCurrent(e)
//    }
//  }
//}
//        }