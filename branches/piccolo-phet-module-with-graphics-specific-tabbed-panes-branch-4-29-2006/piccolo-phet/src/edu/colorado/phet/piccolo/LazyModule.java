/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ClockControlPanel;
import edu.colorado.phet.common.view.ControlPanel;

import javax.swing.*;

/**
 * InitializedModule
 * <p/>
 * A PiccoloModule that can defer initialization until the first time it is activated.
 * This class is provided so that simulations with several modules with time-comsuming
 * initialization behavior can start up more quickly.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class LazyModule extends PiccoloModule {
    private boolean initialized;

    /**
     * Constructor
     *
     * @param name
     * @param clock
     */
    public LazyModule( String name, IClock clock ) {
        this( name, clock, false );
    }

    /**
     * Constructor
     *
     * @param name
     * @param clock
     * @param startsPaused
     */
    public LazyModule( String name, IClock clock, boolean startsPaused ) {
        super( name, clock, startsPaused );

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
     * Abstract init() method. Implementations of this in concrete subclasses are where any
     * defered initialization should be done.
     */
    protected abstract void init();

}



