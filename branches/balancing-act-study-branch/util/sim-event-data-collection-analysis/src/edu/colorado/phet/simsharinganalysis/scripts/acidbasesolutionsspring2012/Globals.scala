// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

object Globals {
  val tabs = List("introductionTab", "customSolutionTab")

  //TODO: Instead of hardcoding these strings, could make a new project that depends on ABS and use toString on those enums.
  //Right now we are keeping this project integrated with the sim-event-data-collection-analysis-project for expedience, so haven't done that yet.

  //Solutions
  val water = "water"
  val weakAcid = "weakAcid"
  val strongAcid = "strongAcid"
  val weakBase = "weakBase"
  val strongBase = "strongBase"

  //Views
  val molecules = "molecules"
  val barGraph = "barGraph"
  val liquid = "liquid"

  //Tests
  val phMeter = "phMeter"
  val phPaper = "phPaper"
  val conductivityTester = "conductivityTester"

  val initialTab0 = Tab0(water, ViewAndTestState(molecules, phMeter, false, 0))
  val initialTab1 = Tab1(ViewAndTestState(molecules, phMeter, false, 0), true, true)
}