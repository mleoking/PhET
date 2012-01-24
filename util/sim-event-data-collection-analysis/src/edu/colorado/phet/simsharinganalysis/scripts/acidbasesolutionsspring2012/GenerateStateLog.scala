// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import java.io.File
import edu.colorado.phet.simsharinganalysis.phet

/**
 * @author Sam Reid
 */

object GenerateStateLog extends App {
  val file = new File("C:\\Users\\Sam\\Desktop\\jc\\2012-01-19_20-21-13_icnq7vebsba23vvhlqetj16pjj_ielmnvgpu1gj103qgmfno54fc4.txt")
  val log = phet parse file
  val states = AcidBaseSolutionSpring2012AnalysisReport.getStates(log)

  println(List("selectedTab", "displayedSolution", "displayedView", "displayedTest",
               "tab0.solution", "tab0.viewAndTestState.view", "tab0.viewAndTestState.test",
               "tab1.acid", "tab1.weak", "tab1.viewAndTestState.view", "tab1.viewAndTestState.test").mkString("\t"))

  states.map(s => List(s.selectedTab, s.displayedSolution, s.displayedView, s.displayedTest,
                       s.tab0.solution, s.tab0.viewAndTestState.view, s.tab0.viewAndTestState.test,
                       s.tab1.acid, s.tab1.weak, s.tab1.viewAndTestState.view, s.tab1.viewAndTestState.test).mkString("\t")).foreach(println)
}