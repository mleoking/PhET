package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

import edu.colorado.phet.simsharinganalysis.RealTimeAnalysis

/**
 * @author Sam Reid
 */
object BAMRealTimeAnalysis extends App {
  new RealTimeAnalysis(log => new BAMReport(log).reportText).main(args)
}
