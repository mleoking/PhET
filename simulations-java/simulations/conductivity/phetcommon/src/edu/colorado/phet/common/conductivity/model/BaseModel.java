/**
 * Class: HelloPhetModel
 * Package: edu.colorado.phet.common.examples.hellophet.model
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.common.conductivity.model;

import edu.colorado.phet.common.conductivity.model.clock.AbstractClock;
import edu.colorado.phet.common.conductivity.model.clock.ClockTickListener;

/**
 * This class is encompasses all the model elements in a physical system. It provides
 * an architecture for executing commands in the model's thread.
 * <p/>
 * Typically, each Module in an application will have its own instance of this
 * class, or a subclass. The application's single ApplicationModel instance will
 * be told which BaseModel is active when Modules are activated.
 */
public class BaseModel extends CompositeModelElement implements ClockTickListener {

    public BaseModel() {
    }

    public void clockTicked( AbstractClock c, double dt ) {
        stepInTime( dt );
    }
}
