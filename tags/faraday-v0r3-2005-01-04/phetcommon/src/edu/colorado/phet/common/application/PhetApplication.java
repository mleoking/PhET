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

import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.PhetFrame;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The top-level class for all PhET applications.
 * It contains a PhetFrame and ApplicationModel.
 *
 * @author ?
 * @version $Revision$
 */
public class PhetApplication {
    private PhetFrame phetFrame;
    private ApplicationModel applicationModel;
    private ModuleManager moduleManager;

    public PhetApplication( ApplicationModel descriptor ) {

        moduleManager = new ModuleManager( this );

        if( descriptor.getModules() == null ) {
            throw new RuntimeException( "Module(s) not specified in ApplicationModel" );
        }
        if( descriptor.getClock() == null ) {
            throw new RuntimeException( "Clock not specified in ApplicationModel" );
        }
        this.applicationModel = descriptor;
        try {
            phetFrame = new PhetFrame( this );
        }
        catch( IOException e ) {
            throw new RuntimeException( "IOException on PhetFrame create.", e );
        }
        moduleManager.addAllModules( descriptor.getModules() );
        s_instance = this;
    }

    public void startApplication() {
        if( applicationModel.getInitialModule() == null ) {
            throw new RuntimeException( "Initial module not specified." );
        }
        moduleManager.setActiveModule( applicationModel.getInitialModule() );
        applicationModel.start();
        phetFrame.setVisible( true );
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ApplicationModel getApplicationModel() {
        return this.applicationModel;
    }

    //
    // Static fields and methods
    //
    private static PhetApplication s_instance = null;

    public static PhetApplication instance() {
        return s_instance;
    }

    public Module moduleAt( int i ) {
        return moduleManager.moduleAt( i );
    }

    public void setActiveModule( int i ) {
        moduleManager.setActiveModule( i );
    }

    public void addModuleObserver( ModuleObserver moduleObserver ) {
        moduleManager.addModuleObserver( moduleObserver );
    }

    public int indexOf( Module m ) {
        return moduleManager.indexOf( m );
    }

    public void addClockTickListener( ClockTickListener clockTickListener ) {
        applicationModel.getClock().addClockTickListener( clockTickListener );
    }

    public void removeClockTickListener( ClockTickListener clockTickListener ) {
        applicationModel.getClock().removeClockTickListener( clockTickListener );
    }

    /**
     * Observes additions and removals of Modules, change in the active Module.
     *
     * @author Ron LeMaster
     * @version $Revision$
     */
    public static interface ModuleObserver {
        public void moduleAdded( Module m );

        public void activeModuleChanged( Module m );

        public void moduleRemoved( Module m );
    }

    /**
     * Maintains a list of all modules in an application, and provides a mechanism for switching
     * which module is active.  (Only supports one active module.)
     */
    public static class ModuleManager {
        private ArrayList modules = new ArrayList();
        private Module activeModule;
        private ArrayList observers = new ArrayList();
        private PhetApplication phetApplication;

        public ModuleManager( PhetApplication phetApplication ) {
            this.phetApplication = phetApplication;
        }

        public Module moduleAt( int i ) {
            return (Module)modules.get( i );
        }

        public Module getActiveModule() {
            return activeModule;
        }

        public int numModules() {
            return modules.size();
        }

        public void addModule( Module module ) {
            addModule( module, false );
        }

        public boolean moduleIsWellFormed( Module module ) {
            boolean result = true;
            result &= module.getModel() != null;
            result &= module.getApparatusPanel() != null;
            return result;
        }

        public void addModule( Module module, boolean isActive ) {

            // Check that the module is well-formed
            if( !moduleIsWellFormed( module ) ) {
                throw new RuntimeException( "Module is missing something." );
            }

            modules.add( module );
            if( isActive ) {
                setActiveModule( module );
            }
            for( int i = 0; i < observers.size(); i++ ) {
                ModuleObserver moduleObserver = (ModuleObserver)observers.get( i );
                moduleObserver.moduleAdded( module );
            }
        }

        public void setActiveModule( int i ) {
            setActiveModule( moduleAt( i ) );
        }

        public void setActiveModule( Module module ) {
            if( activeModule != module ) {
                if( activeModule != null ) {
                    activeModule.deactivate( phetApplication );
//                    activeModule.deactivate( PhetApplication.this );
                }
                activeModule = module;
                module.activate( phetApplication );
//                module.activate( PhetApplication.this );
            }
            for( int i = 0; i < observers.size(); i++ ) {
                ModuleObserver moduleObserver = (ModuleObserver)observers.get( i );
                moduleObserver.activeModuleChanged( module );
            }
        }

        public void addModuleObserver( ModuleObserver observer ) {
            observers.add( observer );
        }

        public int indexOf( Module m ) {
            return modules.indexOf( m );
        }

        public void addAllModules( Module[] modules ) {
            for( int i = 0; i < modules.length; i++ ) {
                addModule( modules[i] );
            }
        }
    }

}
