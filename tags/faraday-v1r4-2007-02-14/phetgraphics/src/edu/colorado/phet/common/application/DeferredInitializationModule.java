/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.application;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ClockControlPanel;
import edu.colorado.phet.common.view.ControlPanel;

import javax.swing.*;

/**
 * DeferredInitializationModule
 * <p/>
 * A PiccoloModule that can defer initialization until the first time it is activated.
 * This class is provided so that simulations with several modules with time-comsuming
 * initialization behavior can start up more quickly.
 * <p/>
 * Any initialization that is to be defered until the first time the module is activated
 * should be put into the implementation of init() (which is abstract in this class) defined
 * in concrete subclasses.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class DeferredInitializationModule extends PhetGraphicsModule {
    private boolean initialized;

    /**
     * Constructor
     *
     * @param name
     * @param clock
     */
    public DeferredInitializationModule( String name, IClock clock ) {
        super ( name, clock );

        // Provide dummy panels for the ModuleManager to reference until we are
        // properly initialized
        setSimulationPanel( new JPanel() );
        setControlPanel( new ControlPanel() );
        setClockControlPanel( new ClockControlPanel( clock ) );
    }

    /**
     * Extends superclass behavior by calling init() the first time this method is
     * invoked.
     */
    public void activate() {
        if( !initialized ) {
            init();
            initialized = true;
        }
        super.activate();
    }

    /**
     * Abstract init() method.
     * <p/>
     * This method is called the first time activate() is called on the module.
     * <p/>
     * Implementations of this in concrete subclasses are where any
     * defered initialization should be done.
     */
    protected abstract void init();

}



