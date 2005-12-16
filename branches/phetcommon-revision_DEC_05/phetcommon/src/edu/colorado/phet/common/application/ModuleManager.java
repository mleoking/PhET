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

import java.beans.XMLEncoder;
import java.util.ArrayList;

/**
 * ModuleManager
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleManager {

    private ArrayList modules = new ArrayList();
    private Module activeModule;
    private PhetApplication phetApplication;
    private ArrayList moduleObservers = new ArrayList();

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
        // Check that the module is well-formed
        if( !moduleIsWellFormed( module ) ) {
            throw new RuntimeException( "Module is missing something." );
        }

        modules.add( module );
        notifyModuleAdded( new ModuleEvent( this, module ) );
    }

    public boolean moduleIsWellFormed( Module module ) {
        return module.moduleIsWellFormed();
    }

    private void notifyModuleAdded( ModuleEvent event ) {
        for( int i = 0; i < moduleObservers.size(); i++ ) {
            ModuleObserver moduleObserver = (ModuleObserver)moduleObservers.get( i );
            moduleObserver.moduleAdded( event );
        }
    }

    public void removeModule( Module module ) {
        modules.remove( module );

        // If the module we are removing is the active module, we need to
        // set another one active
        if( getActiveModule() == module ) {
            setActiveModule( modules.size() == 0 ? null : (Module)modules.get( 0 ) );
        }
        // Notifiy listeners
        notifyModuleRemoved( new ModuleEvent( this, module ) );
    }

    private void notifyModuleRemoved( ModuleEvent event ) {
        for( int i = 0; i < moduleObservers.size(); i++ ) {
            ModuleObserver moduleObserver = (ModuleObserver)moduleObservers.get( i );
            moduleObserver.moduleRemoved( event );
        }
    }

    public void setActiveModule( int i ) {
        setActiveModule( moduleAt( i ) );
    }

    public void setActiveModule( Module module ) {
        if( activeModule != module ) {
            forceSetActiveModule( module );
        }
    }

    void forceSetActiveModule( Module module ) {
        deactivateCurrentModule();
        activate( module );
        notifyActiveModuleChanged( new ModuleEvent( this, module ) );
    }

    private void notifyActiveModuleChanged( ModuleEvent event ) {
        for( int i = 0; i < moduleObservers.size(); i++ ) {
            ModuleObserver moduleObserver = (ModuleObserver)moduleObservers.get( i );
            moduleObserver.activeModuleChanged( event );
        }
    }

    private void activate( Module module ) {
        activeModule = module;
        if( module != null ) {
            module.activate();
            phetApplication.setActiveModule( module );
        }
    }

    public void deactivateCurrentModule() {
        if( activeModule != null ) {
            activeModule.deactivate();
        }
    }

    public void addModuleObserver( ModuleObserver observer ) {
        moduleObservers.add( observer );
    }

    public int indexOf( Module m ) {
        return modules.indexOf( m );
    }

    public void addAllModules( Module[] modules ) {
        for( int i = 0; i < modules.length; i++ ) {
            addModule( modules[i] );
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Save/restore methods
    //
    public void saveStateToConsole() {
        for( int i = 0; i < modules.size(); i++ ) {
            Module module = (Module)modules.get( i );
            XMLEncoder encoder = new XMLEncoder( System.out );
            encoder.writeObject( module );
            encoder.close();
        }
    }

    /**
     * Returns the array of the modules the module manager manages
     *
     * @return the array of the modules the module manager manages
     */
    public Module[] getModules() {
        Module[] moduleArray = new Module[this.modules.size()];
        for( int i = 0; i < modules.size(); i++ ) {
            moduleArray[i] = (Module)modules.get( i );
        }
        return moduleArray;
    }

    public void pause() {
        getActiveModule().deactivate();
    }

    public void resume() {
        getActiveModule().activate();
    }

    public PhetApplication getPhetApplication() {
        return phetApplication;
    }
}
