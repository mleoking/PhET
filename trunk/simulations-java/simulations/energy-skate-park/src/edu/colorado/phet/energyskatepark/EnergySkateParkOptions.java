// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.energyskatepark.view.swing.LocationControlPanel;

/**
 * Author: Sam Reid
 * May 30, 2007, 1:43:35 PM
 */
public class EnergySkateParkOptions {
    private LocationControlPanel.PlanetButtonLayout planetButtonLayout;
    private boolean centered;

    public EnergySkateParkOptions( LocationControlPanel.PlanetButtonLayout planetButtonLayout, boolean centered ) {
        this.planetButtonLayout = planetButtonLayout;
        this.centered = centered;
    }

    public EnergySkateParkOptions() {
//        this( new LocationControlPanel.VerticalPlanetButtonLayout(), false );
        this( new LocationControlPanel.TwoColumnLayout(), false );
    }

    public LocationControlPanel.PlanetButtonLayout getPlanetButtonLayout() {
        return planetButtonLayout;
    }

    public boolean getPlanetButtonsCentered() {
        return centered;
    }
}
