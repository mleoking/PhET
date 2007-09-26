/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.opticaltweezers.model.IDNASpeedStrategy.KathyAbstractDNASpeedStrategy.KathyDNASpeedStrategyB;

/**
 * EnzymeB is one type of fictitious enzyme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeB extends AbstractEnzyme {
    
    public EnzymeB( Point2D position, double outerDiameter, double innerDiameter, 
            DNAStrand dnaStrandBead, DNAStrand dnaStrandFree, Fluid fluid, double maxDt ) {
        super( position, outerDiameter, innerDiameter, 
                dnaStrandBead, dnaStrandFree, fluid, maxDt,
                new KathyDNASpeedStrategyB() );
    }
}
