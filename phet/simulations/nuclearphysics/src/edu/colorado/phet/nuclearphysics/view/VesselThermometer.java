/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.instrumentation.Thermometer;
import edu.colorado.phet.nuclearphysics.model.Vessel;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * VesselThermometer
 * <p/>
 * Extends Thermometer to add a listener to a Vessel to get changes in its tempterature
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VesselThermometer extends Thermometer {

    public VesselThermometer( Component component,
                              Point2D.Double location,
                              double maxScreenLevel,
                              double thickness,
                              boolean isVertical,
                              double minLevel,
                              double maxLevel,
                              Vessel vessel ) {
        super( component, location, maxScreenLevel, thickness, isVertical, minLevel, maxLevel );

        // Add a listener to the vessel that gets changes in temperature
        vessel.addChangeListener( new Vessel.ChangeListener() {
            public void temperatureChanged( Vessel.ChangeEvent event ) {
                VesselThermometer.this.setValue( event.getVessel().getTemperature() );
            }
        } );
    }
}
