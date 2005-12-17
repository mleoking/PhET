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

import java.util.ArrayList;

/**
 * ModuleManager
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class ModuleManager {

    private ArrayList modules = new ArrayList();
    private Module activeModule;
    private PhetApplication phetApplication;
    private ArrayList moduleObservers = new ArrayList();

    ModuleManager( PhetApplication phetApplication ) {
        this.phetApplication = phetApplication;
    }

    Module moduleAt( int i ) {
        return (Module)modules.get( i );
    }

    Module getActiveModule() {
        return activeModule;
    }

    int numModules() {
        return modules.size();
    }

    void addModule( Module module ) {
        // Check that the module is well-formed
        if( !moduleIsWellFormed( module ) ) {
            throw new RuntimeException( "Module is missing something." );
        }

        modules.add( module );
        notifyModuleAdded( new ModuleEvent( getPhetApplication(), module ) );
    }

    boolean moduleIsWellFormed( Module module ) {
        return module.moduleIsWellFormed();
    }

    private void notifyModuleAdded( ModuleEvent event ) {
        for( int i = 0; i < moduleObservers.size(); i++ ) {
            ModuleObserver moduleObserver = (ModuleObserver)moduleObservers.get( i );
            moduleObserver.moduleAdded( event );
        }
    }

    void removeModule( Module module ) {
        modules.remove( module );

        // If the module we are removing is the active module, we need to
        // set another one active
        if( getActiveModule() == module ) {
            setActiveModule( modules.size() == 0 ? null : (Module)modules.get( 0 ) );
        }
        // Notifiy listeners
        notifyModuleRemoved( new ModuleEvent( getPhetApplication(), module ) );
    }

    private void notifyModuleRemoved( ModuleEvent event ) {
        for( int i = 0; i < moduleObservers.size(); i++ ) {
            ModuleObserver moduleObserver = (ModuleObserver)moduleObservers.get( i );
            moduleObserver.moduleRemoved( event );
        }
    }

    void setActiveModule( int i ) {
        setActiveModule( moduleAt( i ) );
    }

    void setActiveModule( Module module ) {
        if( activeModule != module ) {
            forceSetActiveModule( module );
        }
    }

    void forceSetActiveModule( Module module ) {
        deactivateCurrentModule();
        activate( module );
        notifyActiveModuleChanged( new ModuleEvent( getPhetApplication(), module ) );
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

    void deactivateCurrentModule() {
        if( activeModule != null ) {
            activeModule.deactivate();
        }
    }

    void addModuleObserver( ModuleObserver observer ) {
        moduleObservers.add( observer );
    }

    int indexOf( Module m ) {
        return modules.indexOf( m );
    }

    void addAllModules( Module[] modules ) {
        for( int i = 0; i < modules.length; i++ ) {
            addModule( modules[i] );
        }
    }

    /**
     * Returns the array of the modules the module manager manages
     *
     * @return the array of the modules the module manager manages
     */
    Module[] getModules() {
        Module[] moduleArray = new Module[this.modules.size()];
        for( int i = 0; i < modules.size(); i++ ) {
            moduleArray[i] = (Module)modules.get( i );
        }
        return moduleArray;
    }

    void pause() {
        getActiveModule().deactivate();
    }

    void resume() {
        getActiveModule().activate();
    }

    PhetApplication getPhetApplication() {
        return phetApplication;
    }

    public void setModules( Module[] modules ) {
        while( numModules() > 0 ) {
            Module module = moduleAt( 0 );
            removeModule( module );
        }
        addAllModules( modules );
    }
}
