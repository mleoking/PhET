/**
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.common_cck.model;

import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockTickListener;

/**
 * This class is encompasses all the model elements in a physical system. It provides
 * an architecture for executing commands in the model's thread.
 * <p/>
 * Typically, each Module in an application will have its own instance of this
 * class, or a subclass. The application's single ApplicationModel instance will
 * be told which BaseModel is active when Modules are activated.
 */
public class BaseModel extends CompositeModelElement implements ClockTickListener {

    private CommandQueue commandList = new CommandQueue();

    //Not allowed to mess with the way we call our abstract method.
    public void stepInTime( double dt ) {
        commandList.doIt();
        super.stepInTime( dt );
    }

//    protected CommandQueue getCommandList() {
//        return commandList;
//    }

    /**
     * Executes a command on the model. If the model's clock is running, the command
     * is placed on its command queue so that it will be executed the next time
     * the model thread ticks.
     */
    public synchronized void execute( Command cmd ) {
        commandList.addCommand( cmd );
    }

    public void clockTicked( AbstractClock c, double dt ) {
        stepInTime( dt );
    }
}
