// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Chromium
 *
 * @author Ron LeMaster
 */
public class Chromium extends Ion {

    public static final double RADIUS = 6;
    private static IonProperties ionProperties = new IonProperties( 24, 1, RADIUS );

    public Chromium() {
        super( ionProperties );
    }
}