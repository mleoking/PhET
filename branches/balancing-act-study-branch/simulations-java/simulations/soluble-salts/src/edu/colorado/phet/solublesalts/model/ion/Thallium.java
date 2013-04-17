// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Chromium
 *
 * @author Ron LeMaster
 */
public class Thallium extends Ion {

    public static final double RADIUS = 4;
    private static IonProperties ionProperties = new IonProperties( 204, 1, Thallium.RADIUS );

    public Thallium() {
        super( Thallium.ionProperties );
    }
}