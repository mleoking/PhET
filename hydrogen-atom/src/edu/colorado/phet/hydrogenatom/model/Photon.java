/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.util.ColorUtils;

/**
 * Photon is the model of a photon.
 * A photon has a position and direction of motion.
 * It also has an immutable wavelength.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Photon extends MovingObject {

    private double _wavelength;
    
    public Photon( Point2D position, double direction, double wavelength ) {
        super( position, direction );
        if ( wavelength < 0 ) {
            throw new IllegalArgumentException( "invalid wavelength: " + wavelength );
        }
        _wavelength = wavelength;
    }
    
    public double getWavelength() {
        return _wavelength;
    }
    
    public Color getColor() {
        return ColorUtils.wavelengthToColor( _wavelength );
    }
}
