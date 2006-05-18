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
import edu.colorado.phet.piccolo.PiccoloModule;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * InitializedModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class InitializableModule extends PiccoloModule {

    public InitializableModule( String name, IClock clock ) {
        super( name, clock );
    }

    public InitializableModule( String name, IClock clock, boolean startsPaused ) {
        super( name, clock, startsPaused );
    }

    //--------------------------------------------------------------------------------------------------
    // Event mechanism needed for lazy initialization
    //--------------------------------------------------------------------------------------------------
    List activationListeners = new ArrayList();

    public void addListener( ActivationListener activationListener ) {
        activationListeners.add( activationListener );
    }

    public void removeListener( ActivationListener activationListener ) {
        activationListeners .remove( activationListener );
    }

    static interface ActivationListener extends EventListener {
        void activated();
    }

    public interface ModuleInitializer {
        void attach( InitializableModule module );
    }

    //--------------------------------------------------------------------------------------------------
    // Modifications to Module
    //--------------------------------------------------------------------------------------------------

    /**
     * Constructor - Add a parameter
     *
     * @param mi Bridge to a strategy to initialize the module
     */
    private InitializableModule( String name, IClock clock, ModuleInitializer mi ) {
        super( name, clock );
        setSimulationPanel( new JPanel() );
        setControlPanel( new ControlPanel() );
        setClockControlPanel( new ClockControlPanel( clock ) );
        mi.attach( this );
    }

    /**
     * Add notification to listeners
     */
    public void activate() {
        // Notify listeners
        for( int i = 0; i < activationListeners.size(); i++ ) {
            ActivationListener activationListener = (ActivationListener)activationListeners.get( i );
            activationListener.activated();
        }

        // Current activation code
        super.activate();
    }

    /**
     * Abstract init() method
     */
    protected abstract void init();

    //--------------------------------------------------------------------------------------------------
    // Strategies to bridge to
    //--------------------------------------------------------------------------------------------------

    /**
     * Lazy initializer
     */
    public static class LazyInitializer implements ModuleInitializer, ActivationListener {
        boolean initialized;
        private InitializableModule module;

        public void attach( InitializableModule module ) {
            this.module = module;
            module.addListener( this );
        }

        public void activated() {
            if( !initialized ) {
                module.init();
                initialized = true;
            }
        }
    }

    /**
     * Eager initializer
     */
    private static class EagerInitializer implements ModuleInitializer {
        public void attach( InitializableModule module ) {
            module.init();
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Module.Eager and Module.Lazy classes
    //--------------------------------------------------------------------------------------------------

    public static abstract class Eager extends InitializableModule {
        public Eager( String name, IClock clock ) {
            super( name, clock, new EagerInitializer() );
        }
    }

    public static abstract class Lazy extends InitializableModule {
        public Lazy( String name, IClock clock ) {
            super( name, clock, new LazyInitializer() );
        }
    }
}



