// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.awt.*;

/**
 * All units in SI unless otherwise noted
 */
public interface PlateModel {
    public double getElevation( double x, double z );

    public double getDensity( double x, double y ); // z = 0 (cross section plate)

    public double getTemperature( double x, double y );
}
