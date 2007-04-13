/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.EventObject;

/**
 * HeatSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeatSource implements ModelElement {
    private double heatChangePerClockTick;
    private SolubleSaltsModel model;

    public HeatSource( SolubleSaltsModel model ) {
        this.model = model;
    }

    public void stepInTime( double dt ) {
        model.addHeat( heatChangePerClockTick );
    }

    public void setHeatChangePerClockTick( double heatChangePerClockTick ) {
        this.heatChangePerClockTick = heatChangePerClockTick;
    }


    //----------------------------------------------------------------
    // Events and Listeners
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public class ChangeEvent extends EventObject {
        public ChangeEvent( HeatSource source ) {
            super( source );
        }

        public HeatSource getHeatSource() {
            return (HeatSource)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }


}
