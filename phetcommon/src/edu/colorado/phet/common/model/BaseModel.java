/**
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.model.command.CommandQueue;

/**
 * This class is encompasses all the model elements in a physical system. It provides
 * an architecture for executing commands in the model's thread.
 * <p/>
 * Typically, each Module in an application will have its own instance of this
 * class, or a subclass. The application's single ApplicationModel instance will
 * be told which BaseModel is active when Modules are activated.
 */
public class BaseModel extends CompositeModelElement implements ClockTickListener {

    // FixedClock owns the ModelElement it ticks to
    private CommandQueue commandList = new CommandQueue();
    //    private AbstractClock clock;
    //
    //    public BaseModel( AbstractClock clock ) {
    //        this.clock = clock;
    //    }
    //
    //    public void setClock( AbstractClock clock ) {
    //        this.clock = clock;
    //    }
    //
    //    public AbstractClock getClock() {
    //        return clock;
    //    }

    //Not allowed to mess with the way we call our abstract method.
    public final void stepInTime( double dt ) {
        commandList.doIt();
        super.stepInTime( dt );
    }

    /**
     * Executes a command on the model. If the model's clock is running, the command
     * is placed on its command queue so that it will be executed the next time
     * the model thread ticks. If the model's clock is not running, the command
     * is executed immediately.
     */
    public synchronized void execute( Command cmd ) {
        //        if( !clock.isRunning() ) {
        //            cmd.doIt();
        //        }

        commandList.addCommand( cmd );

    }
    //
    public void clockTicked( AbstractClock c, double dt ) {
        stepInTime( dt );
    }
}
