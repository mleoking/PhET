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

  def reportLine(group: Group, filtered: List[AcidBaseReport]) {
    println(group.name + ": " + filtered.length + "/" + group.size + ", sessions = " + filtered.map(_.session) + ", filenames = " + filtered.map(_.filename))
  }

  def report(name: String, filter: AcidBaseReport => Boolean) {
    println(name + ":")
    for ( group <- groups ) {
      reportLine(group, group.reports.filter(filter))
    }
    println("\n")
  }

  def main(args: Array[String]) {
    report("Clicked on ph meter radio button but didn't dunk ph meter", r => r.used("phMeterRadioButton") && !r.dunkedPHMeter)
    report("Clicked on ph paper radio button but didn't dunk ph paper", r => r.used("phPaperRadioButton") && !r.dunkedPHPaper)
    report("Clicked on conductivity tester radio button but didn't dunk conductivity tester", r => r.used("conductivityTesterRadioButton") && !r.completedCircuit)
    report("Moved the conductivity probes but never completed the circuit", r => r.movedConductivityProbesButDidNotCompleteCircuit)
    report("Never clicked on acidRadioButton", r => r.neverUsed("acidRadioButton"))
    report("Never clicked on magnifyingGlassRadioButton", r => r.neverUsed("magnifyingGlassRadioButton"))
  }
}
