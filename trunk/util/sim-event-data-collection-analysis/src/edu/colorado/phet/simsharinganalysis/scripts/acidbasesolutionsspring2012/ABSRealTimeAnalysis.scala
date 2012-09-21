// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import edu.colorado.phet.simsharinganalysis.RealTimeAnalysis

/**
 * @author Sam Reid
 */
object ABSRealTimeAnalysis extends App {

  //  new RealTimeAnalysis(log => new AcidBaseReport(log).reportText).main(args)

  new RealTimeAnalysis(log => {
    val report = new AcidBaseReport(log)
    //    val nonEmptyDragBatches = log.entries.map(e => AcidBaseReport.sliderDragBatchWithPreviousEvent(log, e)).filter(_ != Nil)
    //    val out = if ( nonEmptyDragBatches.length > 0 ) {
    //      val batch = nonEmptyDragBatches.last
    //      batch.mkString("\n") + "\nDecimated:\n"+AcidBaseReport.decimate(batch).mkString("\n")
    //    } else ""
    "clicks: " + report.clicks.length //+"\n\n"+out

  }).main(args)
}