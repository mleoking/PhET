/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.model.ModelElement;


/**
 * ACSource
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ACSource extends AbstractVoltageSource implements ModelElement {

    private double _deltaVoltage;
    
    public ACSource() {
        super();
        _deltaVoltage = 1; //XXX
    }
    
    /*
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
        if ( isEnabled() ) {
           double maxVoltage = getMaxVoltage();
           double voltage = getVoltage() + _deltaVoltage;
           if ( Math.abs( voltage ) > maxVoltage ) {
               voltage = getVoltage() - _deltaVoltage;
           }
           setVoltage( voltage );
        }
    }
    
}
