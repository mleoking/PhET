package edu.colorado.phet.acidbasesolutions;

import java.awt.Image;
import java.awt.image.BufferedImage;


public class ABSImages {

    private ABSImages() {}
    
    public static final Image A_MINUS_MOLECULE = getImage( "A_minus.png" );
    public static final Image B_MOLECULE = getImage( "B.png" );
    public static final Image BH_PLUS_MOLECULE = getImage( "BH_plus.png" );
    public static final Image H2O_MOLECULE = getImage( "H2O.png" );
    public static final Image H3O_PLUS_MOLECULE = getImage( "H3O_plus.png" );
    public static final Image HA_MOLECULE = getImage( "HA.png" );
    public static final Image M_PLUS_MOLECULE = getImage( "M_plus.png" );
    public static final Image MOH_PLUS_MOLECULE = getImage( "MOH.png" );
    public static final Image OH_MINUS_MOLECULE = getImage( "OH_minus.png" );
    
    private static final BufferedImage getImage( String resourceName ) {
        return ABSResources.getImage( resourceName );
    }
}
