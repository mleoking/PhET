package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import java.io.File
import edu.colorado.phet.simsharinganalysis.scripts.rpal2012.RPALAnalysis.Report
import edu.colorado.phet.simsharinganalysis.phet
import edu.colorado.phet.simsharinganalysis.util.SimpleTextFrame

/**
 * @author Sam Reid
 */

object RPALShowAllReports {
  def main(args: Array[String]) {
    val logs = phet load new File(args(0))
    val reports = logs.map(log => new Report(log))
    val string = reports.map(report => {
      "#####################\n" + report.log.file.getName + "\n" + report.toString
    }).mkString("\n")
    new SimpleTextFrame {
      text = string
    }.visible = true
  }

}