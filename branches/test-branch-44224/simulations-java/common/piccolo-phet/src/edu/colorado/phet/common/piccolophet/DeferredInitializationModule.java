/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14676 $
 * Date modified : $Date:2007-04-17 02:58:50 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.piccolophet;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;

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
 * @version $Revision:14676 $
 */
public abstract class DeferredInitializationModule extends PiccoloModule {
    private boolean initialized;

    /**
     * Constructor
     *
     * @param name
     * @param clock
     */
    public DeferredInitializationModule( String name, IClock clock ) {
        this( name, clock, false );
    }

    /**
     * Constructor
     *
     * @param name
     * @param clock
     * @param startsPaused
     */
    public DeferredInitializationModule( String name, IClock clock, boolean startsPaused ) {
        super( name, clock, startsPaused );

        // Provide dummy panels for the ModuleManager to reference until we are
        // properly initialized
        setSimulationPanel( new JPanel() );
        setControlPanel( new ControlPanel() );
        setClockControlPanel( new PiccoloClockControlPanel( clock ) );
    }

    /**
     * Extends superclass behavior by calling init() the first time this method is
     * invoked.
     */
    public void activate() {
        if ( !initialized ) {
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



