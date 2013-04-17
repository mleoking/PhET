// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis.simEvents

/**
 * @author Sam Reid
 */

object DoProcessEvents {
  val simEventMap = Map("Molecule Shapes" -> simEvents.moleculeShapes,
                        "Molecule Polarity" -> simEvents.moleculePolarity,
                        "Balancing Chemical Equations" -> simEvents.balancingChemicalEquations)
}