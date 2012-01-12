// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.enums;

/**
 * DeBroglieView enumerates the different "view" representations for the deBroglie model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public enum DeBroglieView {
    BRIGHTNESS_MAGNITUDE, // Magnitude of amplitude is mapped to brightness in 2D
    BRIGHTNESS, // Amplitude is mapped to brightness in 2D
    RADIAL_DISTANCE, // Amplitude is mapped to radial distance in 2D
    HEIGHT_3D // Amplitude is mapped to height in 3D
}