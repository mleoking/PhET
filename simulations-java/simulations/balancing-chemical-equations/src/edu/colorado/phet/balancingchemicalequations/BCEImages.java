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
    public static final BufferedImage CH4 = getMoleculeImage( "CH4.png" );
    public static final BufferedImage CO2 = getMoleculeImage( "CO2.png" );
    public static final BufferedImage H2 = getMoleculeImage( "H2.png" );
    public static final BufferedImage H2O = getMoleculeImage( "H2O.png" );
    public static final BufferedImage N2 = getMoleculeImage( "N2.png" );
    public static final BufferedImage NH3 = getMoleculeImage( "NH3.png" );
    public static final BufferedImage O2 = getMoleculeImage( "O2.png" );

    // atoms
    public static final BufferedImage C = getMoleculeImage( "C.png" );
    public static final BufferedImage H = getMoleculeImage( "H.png" );
    public static final BufferedImage N = getMoleculeImage( "N.png" );
    public static final BufferedImage O = getMoleculeImage( "O.png" );
    public static final BufferedImage S = getMoleculeImage( "O.png" );

    private static final BufferedImage getMoleculeImage( String name ) {
        return BCEResources.getImage( "molecules/" + name );
    }
}
