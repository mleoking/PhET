// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.opticaltweezers.model.TomDNASpeedStrategy.TomDNASpeedStrategyA;


/**
 * EnzymeA is one type of fictitious enzyme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeA extends AbstractEnzyme {
    
    private static final IDNASpeedStrategy DNA_SPEED_STRATEGY = new TomDNASpeedStrategyA();
    private static final IStallForceStrategy STALL_FORCE_STRATEGY = new ConstantStallForceStrategy( 6 /* pN */ );
    
    public EnzymeA( Point2D position, double outerDiameter, double innerDiameter, 
            DNAStrand dnaStrandBead, DNAStrand dnaStrandFree, Fluid fluid, double maxDt ) {
        super( position, outerDiameter, innerDiameter, dnaStrandBead, dnaStrandFree, fluid, maxDt, DNA_SPEED_STRATEGY, STALL_FORCE_STRATEGY );
    }
}
