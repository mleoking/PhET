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
    report("Clicked on ph meter radio button but didn't dunk ph meter", r => r.used("phMeterRadioButton", "phMeterIcon") && !r.dunkedPHMeter)
    report("Clicked on ph paper radio button but didn't dunk ph paper", r => r.used("phPaperRadioButton", "phPaperIcon") && !r.dunkedPHPaper)
    report("Clicked on conductivity tester radio button but didn't dunk conductivity tester", r => r.used("conductivityTesterRadioButton", "conductivityTesterIcon") && !r.completedCircuit)
    report("Moved the conductivity probes but never completed the circuit", r => r.movedProbe && !r.completedCircuit)
    report("Never clicked on acidRadioButton", r => r.neverUsed("acidRadioButton")) //no icon for this one on the 2nd tab.
    report("Never clicked on magnifyingGlassRadioButton", r => r.neverUsed("magnifyingGlassRadioButton", "magnifyingGlassIcon"))
    report("Never clicked on magnifying glass radio button (1st tab)", r => r.states.filter(_.tab0.viewAndTestState.numTimesPressedMagnifyingGlassRadioButton > 0).length == 0)
    report("Never clicked on magnifying glass radio button (2nd tab)", r => r.states.filter(_.tab1.viewAndTestState.numTimesPressedMagnifyingGlassRadioButton > 0).length == 0)
    report("Never used acid solution controls (2nd tab)", r => r.neverUsedAcidSolutionControls)
    report("Never used base solution controls (2nd tab)", r => r.neverUsedBaseSolutionControls)
    report("Used phmeter but never dunked it", r => r.used("phMeter") && !r.dunkedPHMeter)
    report("Used phPaper but never dunked it", r => r.used("phPaper") && !r.dunkedPHPaper)
    report("Clicked on baseRadioButton", r => r.used("baseRadioButton"))
    report("Clicked on baseRadioButton and then clicked on acidRadioButton", r => {
      r.baseIndices.length > 0 && r.acidIndices.length > 0 &&
      r.baseIndices.min < r.acidIndices.max
    })
  }
}
