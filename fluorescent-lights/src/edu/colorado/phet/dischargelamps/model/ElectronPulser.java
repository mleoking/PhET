/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.common.model.ModelElement;

/**
 * ElectronPulser
 *
 * Puts current into the circuit and fires an electron
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronPulser implements ModelElement {
    private double elapsedTime;
    private double duration = 100;
    private DischargeLampModel model;

    public ElectronPulser( DischargeLampModel model, double current ) {
        this.model = model;
        model.setCurrent( current );
    }

    public void stepInTime( double dt ) {
        elapsedTime += dt;
        if( elapsedTime >= duration ) {
            model.setCurrent( 0 );
            model.removeModelElement( this );
            if( model.getVoltage() > 0 ) {
                model.getLeftHandPlate().produceElectron();
            }
            else if( model.getVoltage() < 0 ) {
                model.getRightHandPlate().produceElectron();
            }
        }
    }
}
