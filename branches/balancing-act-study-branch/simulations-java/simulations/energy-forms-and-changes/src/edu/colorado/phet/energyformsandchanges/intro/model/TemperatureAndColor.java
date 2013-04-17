// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;

/**
 * Convenience class that combines values for temperature and color.  Used
 * primarily by the thermometer to obtain information from the model.
 *
 * @author John Blanco
 */
public class TemperatureAndColor {
    public final double temperature;
    public final Color color;

    public TemperatureAndColor( double temperature, Color color ) {
        this.temperature = temperature;
        this.color = color;
    }
}
