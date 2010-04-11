package edu.colorado.phet.cckscala.tests

class CompanionMNA

class Capacitor(val node0: Int, val node1: Int, val capacitance: Double) extends Element
class Inductor(val node0: Int, val node1: Int, val inductance: Double) extends Element
class ResistiveBattery(val node0: Int, val node1: Int, val voltage: Double, val resistance: Double) extends Element //This models a battery with a resistance in series