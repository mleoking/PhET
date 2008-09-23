/* Copyright University of Colorado, 2003 */
package edu.colorado.phet.common_1200.application;

import edu.colorado.phet.common_1200.model.clock.ClockTickListener;
import edu.colorado.phet.common_1200.view.ApplicationView;
import edu.colorado.phet.common_1200.view.TabbedApparatusPanelContainer;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The top-level class for all PhET applications.
 * It contains an ApplicationView and ApplicationModel.
 */
public class PhetApplication {
    private ApplicationView view;
    private ApplicationModel applicationModel;
    private ModuleManager moduleManager = new ModuleManager();

    public PhetApplication( ApplicationModel descriptor ) {
        if( descriptor.getModules() == null ) {
            throw new RuntimeException( "Module(s) not specified in ApplicationModel" );
        }
        if( descriptor.getClock() == null ) {
            throw new RuntimeException( "Clock not specified in ApplicationModel" );
        }
        this.applicationModel = descriptor;
        JComponent apparatusPanelContainer;
        if( descriptor.numModules() == 1 ) {
            apparatusPanelContainer = new JPanel();
            apparatusPanelContainer.setLayout( new GridLayout( 1, 1 ) );
            if( descriptor.moduleAt( 0 ).getApparatusPanel() == null ) {
                throw new RuntimeException( "Null Apparatus Panel in Module: " + descriptor.moduleAt( 0 ).getName() );
            }
            apparatusPanelContainer.add( descriptor.moduleAt( 0 ).getApparatusPanel() );
        }
        else {
            apparatusPanelContainer = new TabbedApparatusPanelContainer( this );
        }

        try {
            view = new ApplicationView( this.getApplicationDescriptor(), apparatusPanelContainer );
        }
        catch( IOException e ) {
            e.printStackTrace();
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
        view.setVisible( true );
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ApplicationView getApplicationView() {
        return view;
    }

    public ApplicationModel getApplicationDescriptor() {
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

    public void addClockTickListener( ClockListener clockTickListener ) {
        applicationModel.getClock().addClockListener(clockTickListener );
    }

    public void removeClockTickListener( ClockListener clockTickListener ) {
        applicationModel.getClock().removeClockListener( clockTickListener );
    }

    /**
     * Maintains a list of all modules in an application, and provides a mechanism for switching
     * which module is active.  (Only supports one active module.)
     */
    private class ModuleManager {
        private ArrayList modules = new ArrayList();
        private Module activeModule;
        ArrayList observers = new ArrayList();

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
                    activeModule.deactivate( PhetApplication.this );
                }
                activeModule = module;
                module.activate( PhetApplication.this );
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
