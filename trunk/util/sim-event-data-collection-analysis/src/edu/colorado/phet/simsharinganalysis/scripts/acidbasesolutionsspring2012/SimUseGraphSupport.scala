package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import java.io.File
import edu.colorado.phet.simsharinganalysis.phet.load
import AcidBaseReport.toReport


/**
 * On 7/13/2012 Julia Chamberlain asked these questions as preparatory material for constructing a new graph for the acid base solutions paper.
 *
 * @author Sam Reid
 */
object SimUseGraphSupport {

  case class Group(name: String, reports: List[AcidBaseReport]) {
    def size = reports.length
  }

  lazy val groups = {
    val folder = new File("C:\\Users\\Sam\\Desktop\\abs-study-data")
    val files = GroupComparisonTool.filesAt(folder)

    Group("a1", load(files.filter(_.getName.indexOf("_a1_") >= 0)) map toReport) ::
    Group("a2", load(files.filter(_.getName.indexOf("_a2_") >= 0)) map toReport) ::
    Group("a3", load(files.filter(_.getName.indexOf("_a3_") >= 0)) map toReport) :: Nil
  }

  def main(args: Array[String]) {
    println("Clicked on ph meter radio button but didn't dunk ph meter:")
    for ( group <- groups ) {
      println(group.name + ": " + group.reports.count(r => r.used("phMeterRadioButton") && !r.dunkedPHMeter) + "/" + group.size)
    }

    println("\nClicked on ph paper radio button but didn't dunk ph paper:")
    for ( group <- groups ) {
      println(group.name + ": " + group.reports.count(r => r.used("phPaperRadioButton") && !r.dunkedPHPaper) + "/" + group.size)
    }

    println("\nClicked on conductivity tester radio button but didn't dunk conductivity tester:")
    for ( group <- groups ) {
      println(group.name + ": " + group.reports.count(r => r.used("conductivityTesterRadioButton") && !r.completedCircuit) + "/" + group.size)
    }

    println("\nHow many moved the conductivity probes but never completed the circuit:")
    for ( group <- groups ) {
      println(group.name + ": " + group.reports.count(r => r.movedConductivityProbesButDidNotCompleteCircuit) + "/" + group.size)
    }

  }

}
