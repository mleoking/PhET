/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import java.awt.geom.Point2D;

/**
 * BoxHeater
 * <p>
 * An agent that tracks the setting of a TemperatureControl and
 * adjusts the temperature of an MRBox accordingly
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BoxHeater implements TemperatureControl.ChangeListener {
    private MRBox box;


    public BoxHeater( TemperatureControl temperatureControl, MRBox box ) {
        temperatureControl.addChangeListener( this );
        this.box = box;
    }

    public void settingChanged( double setting ) {
        box.setTemperature( setting);
    }

    public void positionChanged( Point2D newPosition ) {
        // noop
    }
}
