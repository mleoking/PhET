// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Hydroxide
 *
 * @author Ron LeMaster
 */
public class Hydroxide extends Ion {
    public static final double RADIUS = 8;
    private static IonProperties ionProperties = new IonProperties( 9, -1, RADIUS );

    public Hydroxide() {
        super( ionProperties );
    }
}