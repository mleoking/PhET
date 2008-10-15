/**
 * Class: HelloPhetModel
 * Package: edu.colorado.phet.common.examples.hellophet.model
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.common_microwaves.model;

import edu.colorado.phet.common.phetcommon.model.Command;
import edu.colorado.phet.common.phetcommon.model.CommandQueue;

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

    //Not allowed to mess with the way we call our abstract method.
    public final void stepInTime( double dt ) {
        commandList.doIt();
        super.stepInTime( dt );
    }

    public synchronized void execute( Command mmc ) {
        commandList.addCommand( mmc );
    }

    public void clockTicked( IClock c, double dt ) {
        stepInTime( dt );
    }
}
