/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.enums;

/**
 * DeBroglieView enumerates the different "view" representations for the deBroglie model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieView extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private DeBroglieView( String name ) {
        super( name );
    }
    
    // Magnitude of amplitude is mapped to brightness in 2D
    public static final DeBroglieView BRIGHTNESS_MAGNITUDE = new DeBroglieView( "brightnessMagnitude" );
    // Amplitude is mapped to brightness in 2D
    public static final DeBroglieView BRIGHTNESS = new DeBroglieView( "brightness" );
    // Amplitude is mapped to radial distance in 2D
    public static final DeBroglieView RADIAL_DISTANCE = new DeBroglieView( "radialDistance" );
    // Amplitude is mapped to height in 3D
    public static final DeBroglieView HEIGHT_3D = new DeBroglieView( "height3D" );
    
    /**
     * Retrieves a well type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return the well type that corresponds to name, possibly null
     */
    public static DeBroglieView getByName( String name ) {
        DeBroglieView rep = null;
        if ( BRIGHTNESS_MAGNITUDE.isNamed( name ) ) {
            rep = BRIGHTNESS_MAGNITUDE;
        }
        else if ( BRIGHTNESS.isNamed( name ) ) {
            rep = BRIGHTNESS;
        }
        else if ( RADIAL_DISTANCE.isNamed( name ) ) {
            rep = RADIAL_DISTANCE;
        }
        else if ( HEIGHT_3D.isNamed( name ) ) {
            rep = HEIGHT_3D;
        }
        return rep;
    }
}
