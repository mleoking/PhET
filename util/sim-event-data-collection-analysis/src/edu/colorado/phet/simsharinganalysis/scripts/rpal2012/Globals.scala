// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

object Globals {
  val tabs = List("introductionTab", "customSolutionTab")

  //TODO: Instead of hardcoding these strings, could make a new project that depends on ABS and use toString on those enums.
  //Right now we are keeping this project integrated with the sim-event-data-collection-analysis-project for expedience, so haven't done that yet.

  val initialTab0 = Tab0("cheese")
  val initialTab1 = Tab1("water", "real", "KITTY")
  val initialTab2 = Tab2(1, true, true, Hiding.nothing, -1, Nil)
}