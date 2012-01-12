package edu.colorado.phet.cckscala.tests

import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolver
import edu.colorado.phet.circuitconstructionkit.model.components.{Branch, Wire, Inductor => CCKInductor, Capacitor => CCKCapacitor, Battery => CCKBattery, Resistor => CCKResistor, Filament, Switch}
import edu.colorado.phet.circuitconstructionkit.model.{Circuit => CCKCircuit}
import collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass
import edu.colorado.phet.circuitconstructionkit.model.components.{Bulb, SeriesAmmeter}

class PureScalaSolver extends CircuitSolver
        with IProguardKeepClass { //loaded with reflection, see CCKModel
  trait Adapter extends Element {
    def applySolution(sol: DynamicCircuitSolution) = {
      getComponent.setCurrent(sol.getCurrent(this))
      getComponent.setVoltageDrop(sol.getNodeVoltage(node1) - sol.getNodeVoltage(node0))
    }

    def getComponent: Branch
  }

  class ResistiveBatteryAdapter(c: CCKCircuit, b: CCKBattery) extends ResistiveBattery(c.indexOf(b.getStartJunction), c.indexOf(b.getEndJunction), b.getVoltageDrop, b.getResistance) with Adapter {
    def getComponent = b

    //don't set voltage on the battery; that actually changes its nominal voltage
    override def applySolution(sol: DynamicCircuitSolution) = getComponent.setCurrent(sol.getCurrent(this))
  }
  class ResistorAdapter(c: CCKCircuit, b: Branch) extends Resistor(c.indexOf(b.getStartJunction), c.indexOf(b.getEndJunction), b.getResistance) with Adapter {
    def getComponent = b
  }
  class CapacitorAdapter(c: CCKCircuit, b: CCKCapacitor) extends Capacitor(c.indexOf(b.getStartJunction), c.indexOf(b.getEndJunction), b.getCapacitance) with Adapter {
    def getComponent = b
  }
  class InductorAdapter(c: CCKCircuit, b: CCKInductor) extends Inductor(c.indexOf(b.getStartJunction), c.indexOf(b.getEndJunction), b.getInductance) with Adapter {
    def getComponent = b
  }
  def apply(circuit: CCKCircuit, dt: Double) = {
    val resistiveBatteries = new ArrayBuffer[ResistiveBatteryAdapter]
    val resistors = new ArrayBuffer[ResistorAdapter]
    val capacitors = new ArrayBuffer[(CapacitorAdapter, CState)]
    val inductors = new ArrayBuffer[(InductorAdapter, CState)]
    for (i <- 0 until circuit.numBranches) {
      circuit.getBranches.apply(i) match {
        case battery: CCKBattery => resistiveBatteries += new ResistiveBatteryAdapter(circuit, battery)
        case resistor: CCKResistor => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Wire => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Filament => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Switch => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Bulb => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: SeriesAmmeter => resistors += new ResistorAdapter(circuit, resistor)
        case capacitor: CCKCapacitor => capacitors += {
          val mytuple = (new CapacitorAdapter(circuit, capacitor), new CState(capacitor.getVoltageDrop, capacitor.getCurrent))
          mytuple
        }
        case inductor: CCKInductor => inductors += {
          val mytuple = (new InductorAdapter(circuit, inductor), new CState(inductor.getVoltageDrop, inductor.getCurrent))
          mytuple //TODO: why is this workaround necessary?
        }
      }
    }
    val circ = new DynamicCircuit(Nil, resistors, Nil, resistiveBatteries, capacitors, inductors)
    
    val start = System.currentTimeMillis
    val solution = circ.solveItWithSubdivisions(dt)
    val end = System.currentTimeMillis

    println("elapsed = "+(end-start))

    resistiveBatteries.foreach(_.applySolution(solution))
    resistors.foreach(_.applySolution(solution))
    capacitors.foreach(_._1.applySolution(solution))
    inductors.foreach(_._1.applySolution(solution))
    fireCircuitSolved()
  }
}