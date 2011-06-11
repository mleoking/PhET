// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Chloride
 *
 * @author Ron LeMaster
 */
public class Arsenate extends Ion {
    public static final double RADIUS = 10;
    private static IonProperties ionProperties = new IonProperties( 17, -1, Arsenate.RADIUS );

    public Arsenate() {
        super( Arsenate.ionProperties );
    }
}