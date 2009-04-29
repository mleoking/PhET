package edu.colorado.phet.acidbasesolutions;

import java.awt.Image;
import java.awt.image.BufferedImage;


public class ABSImages {

    private ABSImages() {}
    
    public static final Image A_MINUS_MOLECULE = getImage( "molecules/A_minus.png" );
    public static final Image B_MOLECULE = getImage( "molecules/B.png" );
    public static final Image BH_PLUS_MOLECULE = getImage( "molecules/BH_plus.png" );
    public static final Image H2O_MOLECULE = getImage( "molecules/H2O.png" );
    public static final Image H3O_PLUS_MOLECULE = getImage( "molecules/H3O_plus.png" );
    public static final Image HA_MOLECULE = getImage( "molecules/HA.png" );
    public static final Image M_PLUS_MOLECULE = getImage( "molecules/M_plus.png" );
    public static final Image MOH_PLUS_MOLECULE = getImage( "molecules/MOH.png" );
    public static final Image OH_MINUS_MOLECULE = getImage( "molecules/OH_minus.png" );
    
    public static final Image A_MINUS_STRUCTURE = getImage( "lewis_structures/A_minus.png" );
    public static final Image B_STRUCTURE = getImage( "lewis_structures/B.png" );
    public static final Image BH_PLUS_STRUCTURE = getImage( "lewis_structures/BH_plus.png" );
    public static final Image H2O_STRUCTURE = getImage( "lewis_structures/H2O.png" );
    public static final Image H3O_PLUS_STRUCTURE = getImage( "lewis_structures/H3O_plus.png" );
    public static final Image HA_STRUCTURE = getImage( "lewis_structures/HA.png" );
    public static final Image M_PLUS_STRUCTURE = getImage( "lewis_structures/M_plus.png" );
    public static final Image MOH_PLUS_STRUCTURE = getImage( "lewis_structures/MOH.png" );
    public static final Image OH_MINUS_STRUCTURE = getImage( "lewis_structures/OH_minus.png" );
    
    private static final BufferedImage getImage( String resourceName ) {
        return ABSResources.getImage( resourceName );
    }
}
