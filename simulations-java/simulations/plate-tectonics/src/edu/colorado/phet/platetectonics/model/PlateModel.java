// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;

/**
 * All units in SI unless otherwise noted
 */
public abstract class PlateModel {
    public final VoidNotifier modelChanged = new VoidNotifier();

    public abstract double getElevation( double x, double z );

    public abstract double getDensity( double x, double y ); // z = 0 (cross section plate)

    public abstract double getTemperature( double x, double y );

    public void update( double timeElapsed ) {

    }
}
