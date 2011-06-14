// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

/**
 * Silver
 *
 * @author Ron LeMaster
 */
public class Silver extends Ion {
    public static final double RADIUS = 4;
    private static IonProperties ionProperties = new IonProperties( 47, 1, RADIUS );

    public Silver() {
        super( ionProperties );
    }
}