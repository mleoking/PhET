// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Copper
 *
 * @author Ron LeMaster
 */
public class Mercury extends Ion {
    public static final double RADIUS = 6;
    private static IonProperties ionProperties = new IonProperties( 80, 1, RADIUS );

    public Mercury() {
        super( ionProperties );
    }
}