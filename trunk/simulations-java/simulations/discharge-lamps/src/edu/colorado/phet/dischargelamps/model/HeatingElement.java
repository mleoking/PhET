// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import java.util.EventListener;
import java.util.EventObject;

import edu.colorado.phet.common.phetcommon.model.Particle;
import edu.colorado.phet.common.phetcommon.util.EventChannel;

/**
 * HeatingElement
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeatingElement extends Particle {
    private double temperature;
    private boolean isEnabled;

    public void setTemperature( double temperature ) {
        this.temperature = temperature;
        changeListenerProxy.temperatureChanged( new ChangeEvent( this ) );
    }

    public double getTemperature() {
        return temperature;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled( boolean enabled ) {
        this.isEnabled = enabled;
        changeListenerProxy.isEnabledChanged( new ChangeEvent( this ) );
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener) changeEventChannel.getListenerProxy();

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public HeatingElement getHeatingElement() {
            return (HeatingElement) getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        public void temperatureChanged( ChangeEvent event );

        public void isEnabledChanged( ChangeEvent event );
    }

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
