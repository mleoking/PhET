package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import java.io.File
import edu.colorado.phet.simsharinganalysis.phet.load
import AcidBaseSolutionSpring2012AnalysisReport.toReport


/**
 * On 7/13/2012 Julia Chamberlain asked these questions as preparatory material for constructing a new graph for the acid base solutions paper.
 *
 * @author Sam Reid
 */
object SimUseGraphSupport {

  def groups = {
    val folder = new File("C:\\Users\\Sam\\Desktop\\abs-study-data")
    val files = GroupComparisonTool.filesAt(folder)
    val group1 = files.filter(_.getName.indexOf("_a1_") >= 0) -> "a1"
    val group2 = files.filter(_.getName.indexOf("_a2_") >= 0) -> "a2"
    val group3 = files.filter(_.getName.indexOf("_a3_") >= 0) -> "a3"

    println("calling groups")
    //    println("group 1\n" + group1._1.mkString("\n"))
    //    println("group 2\n" + group2._1.mkString("\n"))
    //    println("group 3\n" + group3._1.mkString("\n"))

    //        val groups = for ( g <- group1 :: group2 :: group3 :: Nil; logs = phet.load(g._1) ) yield {
    //          GroupResult(logs.map(AcidBaseSolutionSpring2012AnalysisReport.toReport(_)).toList, g._2)
    //        }
    group1 :: group2 :: group3 :: Nil
  }

  def numberStudentsClickedOnToolRadioButtonsWithoutDunkingTheTool = {
    for ( group <- groups ) {
      val reports = load(group._1) map toReport
      println("Group: " + group._2 + ", sessions: " + reports.length + ", count=" + reports.count(clickedOnToolRadioButtonsWithoutDunkingTheTool))
    }
  }

  def clickedOnToolRadioButtonsWithoutDunkingTheTool(result: AcidBaseSolutionSpring2012AnalysisReport) = {
    println("clicked ph meter radio button = " + result.clickedPHMeterRadioButton)
    result.clickedPHMeterRadioButton && !result.dunkedPHMeter
  }

  def main(args: Array[String]) {
    println(numberStudentsClickedOnToolRadioButtonsWithoutDunkingTheTool)
  }

}
