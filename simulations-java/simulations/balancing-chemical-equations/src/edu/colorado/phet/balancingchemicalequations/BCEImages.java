// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import java.awt.image.BufferedImage;

/**
 * Images used in this project.
 * Statically loaded so we can easily see if any are missing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEImages {

    /* not intended for instantiation */
    private BCEImages() {}

    // molecules
    public static final BufferedImage CH4 = getImage( "molecules/CH4.png" );
    public static final BufferedImage CO2 = getImage( "molecules/CO2.png" );
    public static final BufferedImage H2 = getImage( "molecules/H2.png" );
    public static final BufferedImage H2O = getImage( "molecules/H2O.png" );
    public static final BufferedImage N2 = getImage( "molecules/N2.png" );
    public static final BufferedImage NH3 = getImage( "molecules/NH3.png" );
    public static final BufferedImage O2 = getImage( "molecules/O2.png" );

    // atoms
    public static final BufferedImage C = getImage( "atoms/C.png" );
    public static final BufferedImage H = getImage( "atoms/H.png" );
    public static final BufferedImage N = getImage( "atoms/N.png" );
    public static final BufferedImage O = getImage( "atoms/O.png" );
    public static final BufferedImage S = getImage( "atoms/O.png" );

    private static final BufferedImage getImage( String name ) {
        return BCEResources.getImage( name );
    }
}
