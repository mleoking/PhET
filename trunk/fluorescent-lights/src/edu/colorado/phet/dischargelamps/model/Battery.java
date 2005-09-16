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

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.util.SimpleObservable;

import java.util.EventObject;
import java.util.EventListener;

/**
 * VoltageSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Battery extends SimpleObservable {
    private boolean enabled = true;
    private double voltage;
    private double minVoltage;
    private double maxVoltage;
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public Battery() {
    }

    public Battery( double minVoltage, double maxVoltage ) {
        this.minVoltage = minVoltage;
        this.maxVoltage = maxVoltage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage( double voltage ) {
        this.voltage = voltage;
        changeListenerProxy.voltageChanged( new ChangeEvent( this ) );
        super.notifyObservers();
    }

    public double getMinVoltage() {
        return minVoltage;
    }

    public void setMinVoltage( double minVoltage ) {
        this.minVoltage = minVoltage;
    }

    public double getMaxVoltage() {
        return maxVoltage;
    }

    public void setMaxVoltage( double maxVoltage ) {
        this.maxVoltage = maxVoltage;
    }

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public Battery getVoltageSource() {
            return (Battery)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        public void voltageChanged( ChangeEvent event );
    }
}
