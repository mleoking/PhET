// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.IonProperties;

/**
 * Sugar molecules are neither charged nor ionized, but we model them this way to efficiently reuse code from salts & solubility for crystallization and random walk
 *
 * @author Sam Reid
 */
public class SugarIon extends Ion {
    public static final double RADIUS = 14;

    //has to be public since loaded with reflection
    public SugarIon( double charge ) {
        super( new IonProperties( 80, charge, RADIUS ) );
    }

    public static class PositiveSugarIon extends SugarIon {
        public PositiveSugarIon() {
            super( +1 );
        }
    }

    public static class NegativeSugarIon extends SugarIon {
        public NegativeSugarIon() {
            super( -1 );
        }
    }
}
