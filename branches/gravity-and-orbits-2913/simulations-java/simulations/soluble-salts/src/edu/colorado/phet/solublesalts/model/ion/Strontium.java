// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Strontium
 *
 * @author Ron LeMaster
 */
public class Strontium extends Ion {

    public static final double RADIUS = 6;
    private static IonProperties ionProperties = new IonProperties( 38, 1, RADIUS );

    public Strontium() {
        super( ionProperties );
    }
}