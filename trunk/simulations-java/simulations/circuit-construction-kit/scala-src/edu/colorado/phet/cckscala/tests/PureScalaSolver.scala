package edu.colorado.phet.cckscala.tests

import circuitconstructionkit.model.analysis.CircuitSolver
import circuitconstructionkit.model.components.{Branch, Wire, Inductor => CCKInductor, Capacitor => CCKCapacitor, Battery => CCKBattery, Resistor => CCKResistor, Filament, Switch, Bulb, SeriesAmmeter}
import circuitconstructionkit.model.{Circuit => CCKCircuit}
import collection.mutable.ArrayBuffer

class PureScalaSolver extends CircuitSolver {
  trait Adapter extends Element{
    def applySolution(sol: CompanionSolution) = {
      getComponent.setCurrent(sol.getCurrent(this))
      getComponent.setVoltageDrop(sol.getVoltage(this))
    }

    def getComponent: Branch
  }
  class BatteryAdapter(c: CCKCircuit, b: CCKBattery)
          extends Battery(c.indexOf(b.getStartJunction), c.indexOf(b.getEndJunction), b.getVoltageDrop) with Adapter {
    def getComponent = b //todo: maybe shouldn't set voltage on the battery..?  On the other hand, hopefully v is coming back correct from the solver
    //todo: though it may not be exactly correct, and perhaps errors will accrue over time
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
    val batteries = new ArrayBuffer[BatteryAdapter]
    val resistors = new ArrayBuffer[ResistorAdapter]
    val capacitors = new ArrayBuffer[CapacitorAdapter]
    val inductors = new ArrayBuffer[InductorAdapter]
    for (i <- 0 until circuit.numBranches) {
      circuit.getBranches.apply(i) match {
        case battery: CCKBattery => batteries += new BatteryAdapter(circuit, battery)
        case resistor: CCKResistor => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Wire => resistors += new ResistorAdapter(circuit, resistor)
        case resistor: Filament => resistors += new ResistorAdapter(circuit, resistor)
        case s: Switch => resistors += new ResistorAdapter(circuit, s)
        case s: Bulb => resistors += new ResistorAdapter(circuit, s)
        case s: SeriesAmmeter => resistors += new ResistorAdapter(circuit, s)
        case c: CCKCapacitor => capacitors += new CapacitorAdapter(circuit, c)
        case c: CCKInductor => inductors += new InductorAdapter(circuit, c)
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