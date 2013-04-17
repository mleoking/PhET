// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Chloride
 *
 * @author Ron LeMaster
 */
public class Bromine extends Ion {
    public static final double RADIUS = 8;
    private static IonProperties ionProperties = new IonProperties( 35, -1, RADIUS );

    public Bromine() {
        super( ionProperties );
    }
}
