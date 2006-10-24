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

import edu.colorado.phet.common.util.EventChannel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * EnergyProfile
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyProfile {

    private double leftLevel;
    private double rightLevel;
    private double peakLevel;
    // The width of the threshold curvel. I.E, the separatio between stable states.
    private double thresholdWidth;
//    private BoundedRangeModel leftRange;
//    private BoundedRangeModel rightRange;
//    private BoundedRangeModel peakRange;

    public EnergyProfile( double leftLevel, double peakLevel, double rightLevel, double thresholdWidth ) {
        this.thresholdWidth = thresholdWidth;
        this.peakLevel = peakLevel;
        this.leftLevel = leftLevel;
        this.rightLevel = rightLevel;
    }

    public double getLeftLevel() {
        return leftLevel;
    }

    public void setLeftLevel( double leftLevel ) {
        this.leftLevel = leftLevel;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public double getRightLevel() {
        return rightLevel;
    }

    public void setRightLevel( double rightLevel ) {
        this.rightLevel = rightLevel;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public double getPeakLevel() {
        return peakLevel;
    }

    public void setPeakLevel( double peakLevel ) {
        this.peakLevel = peakLevel;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public double getThresholdWidth() {
        return thresholdWidth;
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener ( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener ( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
