// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers;

import java.awt.image.BufferedImage;

/**
 * Images used in this project.
 * Statically loaded so we can easily see if any are missing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALImages {

    private RPALImages() {}

    // sandwich images
    public static final BufferedImage BREAD = getSandwichImage( "bread.png" );
    public static final BufferedImage CHEESE = getSandwichImage( "cheese.png" );
    public static final BufferedImage MEAT = getSandwichImage( "meat.png" );
    
    // molecule images
    public static final BufferedImage C = getMoleculeImage( "C.png" );
    public static final BufferedImage C2H2 = getMoleculeImage( "C2H2.png" );
    public static final BufferedImage C2H4 = getMoleculeImage( "C2H4.png" );
    public static final BufferedImage C2H5Cl = getMoleculeImage( "C2H5Cl.png" );
    public static final BufferedImage C2H5OH = getMoleculeImage( "C2H5OH.png" );
    public static final BufferedImage C2H6 = getMoleculeImage( "C2H6.png" );
    public static final BufferedImage CH2O = getMoleculeImage( "CH2O.png" );
    public static final BufferedImage CH3OH = getMoleculeImage( "CH3OH.png" );
    public static final BufferedImage CH4 = getMoleculeImage( "CH4.png" );
    public static final BufferedImage CO = getMoleculeImage( "CO.png" );
    public static final BufferedImage CO2 = getMoleculeImage( "CO2.png" );
    public static final BufferedImage CS2 = getMoleculeImage( "CS2.png" );
    public static final BufferedImage Cl2 = getMoleculeImage( "Cl2.png" );
    public static final BufferedImage F2 = getMoleculeImage( "F2.png" );
    public static final BufferedImage H2 = getMoleculeImage( "H2.png" );
    public static final BufferedImage H2O = getMoleculeImage( "H2O.png" );
    public static final BufferedImage H2S = getMoleculeImage( "H2S.png" );
    public static final BufferedImage HCl = getMoleculeImage( "HCl.png" );
    public static final BufferedImage HF = getMoleculeImage( "HF.png" );
    public static final BufferedImage N2 = getMoleculeImage( "N2.png" );
    public static final BufferedImage N2O = getMoleculeImage( "N2O.png" );
    public static final BufferedImage NH3 = getMoleculeImage( "NH3.png" );
    public static final BufferedImage NO = getMoleculeImage( "NO.png" );
    public static final BufferedImage NO2 = getMoleculeImage( "NO2.png" );
    public static final BufferedImage O2 = getMoleculeImage( "O2.png" );
    public static final BufferedImage OF2 = getMoleculeImage( "OF2.png" );
    public static final BufferedImage P4 = getMoleculeImage( "P4.png" );
    public static final BufferedImage PCl3 = getMoleculeImage( "PCl3.png" );
    public static final BufferedImage PCl5 = getMoleculeImage( "PCl5.png" );
    public static final BufferedImage PF3 = getMoleculeImage( "PF3.png" );
    public static final BufferedImage PH3 = getMoleculeImage( "PH3.png" );
    public static final BufferedImage S = getMoleculeImage( "S.png" );
    public static final BufferedImage SO2 = getMoleculeImage( "SO2.png" );
    public static final BufferedImage SO3 = getMoleculeImage( "SO3.png" );
    
    // all molcule images, used in game
    public static final BufferedImage[] ALL_MOLECULES = {
        C, C2H2, C2H4, C2H5Cl, C2H5Cl, C2H5OH, C2H6, CH2O, CH3OH, CH4, CO, CO2, CS2, Cl2,
        F2, H2, H2O, H2S, HCl, HF, N2, N2O, NH3, NO, NO2, O2, OF2, P4, PCl3, PCl5, PF3, PH3, S, SO2, SO3
    };
    
    private static final BufferedImage getSandwichImage( String name ) {
        return RPALResources.getImage( "sandwich/" + name );
    }
    
    private static final BufferedImage getMoleculeImage( String name ) {
        return RPALResources.getImage( "molecules/" + name );
    }
}
