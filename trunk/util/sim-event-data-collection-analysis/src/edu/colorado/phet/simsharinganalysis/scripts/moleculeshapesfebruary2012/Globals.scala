// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.moleculeshapesfebruary2012

object Globals {
  val tabs = List("introductionTab", "customSolutionTab")

  //TODO: Instead of hardcoding these strings, could make a new project that depends on ABS and use toString on those enums.
  //Right now we are keeping this project integrated with the sim-event-data-collection-analysis-project for expedience, so haven't done that yet.

  //Views
  val molecules = "molecules"
  val barGraph = "barGraph"
  val liquid = "liquid"

  //Tests
  val phMeter = "phMeter"
  val phPaper = "phPaper"
  val conductivityTester = "conductivityTester"

  val initialTab0 = Tab0("dummy")
  val initialTab1 = Tab1("CO2", "real", "KITTY")
}