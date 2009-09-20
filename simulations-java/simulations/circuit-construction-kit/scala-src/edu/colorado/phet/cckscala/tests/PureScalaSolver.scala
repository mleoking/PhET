package edu.colorado.phet.cckscala.tests

import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolver
import edu.colorado.phet.circuitconstructionkit.model.components.{Branch, Wire, Inductor => CCKInductor, Capacitor => CCKCapacitor, Battery => CCKBattery, Resistor => CCKResistor, Filament, Switch, Bulb, SeriesAmmeter}
import edu.colorado.phet.circuitconstructionkit.model.{Circuit => CCKCircuit}
import collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass
import edu.colorado.phet.circuitconstructionkit.model.components.{Bulb, SeriesAmmeter}

class PureScalaSolver extends CircuitSolver
        with IProguardKeepClass { //loaded with reflection, see CCKModel
  trait Adapter extends Element {
    def applySolution(sol: CompanionSolution) = {
      getComponent.setCurrent(sol.getCurrent(this))
      getComponent.setVoltageDrop(sol.getVoltage(this))
    }

    def getComponent: Branch
  }

  class ResistiveBatteryAdapter(c: CCKCircuit, b: CCKBattery)
          extends ResistiveBattery(c.indexOf(b.getStartJunction), c.indexOf(b.getEndJunction), b.getVoltageDrop,b.getResistance) with Adapter {
    def getComponent = b

    //don't set voltage on the battery; that actually changes its nominal voltage
    override def applySolution(sol: CompanionSolution) = getComponent.setCurrent(sol.getCurrent(this))
  }
  class ResistorAdapter(c: CCKCircuit, b: Branch)
          extends Resistor(c.indexOf(b.getStartJunction), c.indexOf(b.getEndJunction), b.getResistance) with Adapter {
    def getComponent = b
  }
  class CapacitorAdapter(c: CCKCircuit, b: CCKCapacitor)
          extends Capacitor(c.indexOf(b.getStartJunction), c.indexOf(b.getEndJunction),
            b.getCapacitance, b.getVoltageDrop, b.getCurrent) with Adapter {
    def getComponent = b
  }
  class InductorAdapter(c: CCKCircuit, b: CCKInductor)
          extends Inductor(c.indexOf(b.getStartJunction), c.indexOf(b.getEndJunction),
            b.getInductance, b.getVoltageDrop, b.getCurrent) with Adapter {
    def getComponent = b
  }
  def apply(circuit: CCKCircuit, dt: Double) = {
    val batteries = new ArrayBuffer[ResistiveBatteryAdapter]
    val resistors = new ArrayBuffer[ResistorAdapter]
    val capacitors = new ArrayBuffer[CapacitorAdapter]
    val inductors = new ArrayBuffer[InductorAdapter]
    for (i <- 0 until circuit.numBranches) {
      circuit.getBranches.apply(i) match {
        case battery: CCKBattery => batteries += new ResistiveBatteryAdapter(circuit, battery)
        case resistor: CCKResistor => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Wire => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Filament => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Switch => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Bulb => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: SeriesAmmeter => resistors += new ResistorAdapter(circuit, resistor)
        case capacitor: CCKCapacitor => capacitors += new CapacitorAdapter(circuit, capacitor)
        case inductor: CCKInductor => inductors += new InductorAdapter(circuit, inductor)
      }
    }
    val circ = new FullCircuit(batteries, resistors, capacitors, inductors)
    val solution = circ.solve(dt)
    batteries.foreach(_.applySolution(solution))
    resistors.foreach(_.applySolution(solution))
    capacitors.foreach(_.applySolution(solution))
    inductors.foreach(_.applySolution(solution))
    fireCircuitSolved()
  }
}