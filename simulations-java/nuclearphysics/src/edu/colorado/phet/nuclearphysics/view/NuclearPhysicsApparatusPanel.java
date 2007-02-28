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

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.ClockEvent;

import java.awt.*;

/**
 * NuclearPhysicsApparatusPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class NuclearPhysicsApparatusPanel extends ApparatusPanel {
    public NuclearPhysicsApparatusPanel( IClock clock ) {
        super( clock );
        clock.addClockListener( new ClockListener() {
            public void clockTicked( ClockEvent clockEvent ) {
                forwardClockEvent( clockEvent );
            }

            public void clockStarted( ClockEvent clockEvent ) {
                forwardClockEvent( clockEvent );
            }

            public void clockPaused( ClockEvent clockEvent ) {
                forwardClockEvent( clockEvent );
            }

            public void simulationTimeChanged( ClockEvent clockEvent ) {
                forwardClockEvent( clockEvent );
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {
                forwardClockEvent( clockEvent );
            }
        } );
    }

    public void handleUserInput() {
        Component[] components = getComponents();
        for( int i = 0; i < components.length; i++ ) {
            Component component = components[i];
            if( component instanceof ApparatusPanel2 ) {
                ApparatusPanel2 apparatusPanel2 = (ApparatusPanel2)component;
                apparatusPanel2.handleUserInput();
            }
        }
    }

    private void forwardClockEvent( ClockEvent clockEvent ) {
        Component[] components = getComponents();
        for( int i = 0; i < components.length; i++ ) {
            Component component = components[i];
            if( component instanceof ApparatusPanel2 ) {
                ApparatusPanel2 apparatusPanel2 = (ApparatusPanel2)component;
                apparatusPanel2.clockTicked( clockEvent );
            }
        }
    }

    public void clockTicked( ClockEvent event ) {
    }
}
