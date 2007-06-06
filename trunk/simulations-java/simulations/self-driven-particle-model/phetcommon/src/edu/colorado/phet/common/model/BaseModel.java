/* Copyright 2003, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/model/BaseModel.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.ClockTickEvent;

/**
 * This class is encompasses all the model elements in a physical system. It provides
 * an architecture for executing commands in the model's thread.
 * <p/>
 * Typically, each Module in an application will have its own instance of this
 * class, or a subclass. The application's single ApplicationModel instance will
 * be told which BaseModel is active when Modules are activated.
 * 
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class BaseModel extends CompositeModelElement implements ClockTickListener {

    private CommandQueue commandList = new CommandQueue();

    //Not allowed to mess with the way we call our abstract method.
    public void stepInTime( double dt ) {
        commandList.doIt();
        super.stepInTime( dt );
    }

    /**
     * Executes a command on the model. If the model's clock is running, the command
     * is placed on its command queue so that it will be executed the next time
     * the model thread ticks.
     */
    public synchronized void execute( Command cmd ) {
        commandList.addCommand( cmd );
    }

    public void clockTicked( ClockTickEvent event ) {
        stepInTime( event.getDt() );
    }
}
