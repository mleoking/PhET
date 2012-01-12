// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Phosphate
 *
 * @author Ron LeMaster
 */
public class Phosphate extends Ion {
    public static final double RADIUS = 8;
    private static IonProperties ionProperties = new IonProperties( 23, -1, RADIUS );

    public Phosphate() {
        super( ionProperties );
    }
}