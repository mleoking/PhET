/* Copyright 2003, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_sound.model;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;

/**
 * This class is encompasses all the model elements in a physical system. It provides
 * an architecture for executing commands in the model's thread.
 * <p/>
 * Typically, each Module in an application will have its own instance of this
 * class, or a subclass. The application's single ApplicationModel instance will
 * be told which BaseModel is active when Modules are activated.
 *
 * @author ?
 * @version $Revision$
 */
public class BaseModel extends CompositeModelElement implements ClockListener {


    //Not allowed to mess with the way we call our abstract method.
    public void stepInTime( double dt ) {
        super.stepInTime( dt );
    }

    public void clockPaused( ClockEvent clockEvent ) {}

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockTicked( ClockEvent clockEvent ) {
        stepInTime( clockEvent.getSimulationTimeChange() );
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {}
}
