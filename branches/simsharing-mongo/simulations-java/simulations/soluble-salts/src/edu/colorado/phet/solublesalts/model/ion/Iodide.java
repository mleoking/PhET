// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Iodine
 *
 * @author Ron LeMaster
 */
public class Iodide extends Ion {
    public static final double RADIUS = 6;
    private static IonProperties ionProperties = new IonProperties( 53, -1, RADIUS );

    public Iodide() {
        super( ionProperties );
    }
}