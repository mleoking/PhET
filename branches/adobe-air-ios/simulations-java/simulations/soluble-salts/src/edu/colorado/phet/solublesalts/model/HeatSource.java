// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.util.EventListener;
import java.util.EventObject;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.EventChannel;

/**
 * HeatSource
 *
 * @author Ron LeMaster
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

    public class ChangeEvent extends EventObject {
        public ChangeEvent( HeatSource source ) {
            super( source );
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