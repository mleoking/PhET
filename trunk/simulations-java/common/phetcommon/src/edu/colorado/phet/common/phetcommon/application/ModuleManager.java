/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.application;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;

import java.util.ArrayList;

/**
 * The ModuleManager keeps track of a list of Modules in a PhetApplication, and which one is active.
 * Notification events are sent to registered listeners when modules are added or removed, or when
 * the active module changes.  There is currently no support for setting the active module to null.
 * <p/>
 * Clients shouldn't use this class directly.  They should use the public interface published by
 * PhetApplication.
 *
 * @author Ron LeMaster & Sam Reid
 * @version $Revision$
 */
class ModuleManager {

    private ArrayList modules = new ArrayList();
    private Module activeModule;
    private PhetApplication phetApplication;
    private ArrayList moduleObservers = new ArrayList();

    /**
     * Constructs a ModuleManager for a PhetApplication.
     *
     * @param phetApplication
     */
    ModuleManager( PhetApplication phetApplication ) {
        this.phetApplication = phetApplication;
    }

    /**
     * Returns the specified module.
     *
     * @param i
     * @return the specified module.
     */
    Module moduleAt( int i ) {
        return (Module)modules.get( i );
    }

    /**
     * Returns the active module, or null if no module has been activated yet.
     *
     * @return the active module, or null if no module has been activated yet.
     */
    Module getActiveModule() {
        return activeModule;
    }

    /**
     * Gets the number of modules.
     *
     * @return the number of modules.
     */
    int numModules() {
        return modules.size();
    }

    /**
     * Add one module.
     *
     * @param module
     */
    void addModule( Module module ) {
        modules.add( module );
        notifyModuleAdded( new ModuleEvent( getPhetApplication(), module ) );
    }

    /**
     * Removes the specified module.
     *
     * @param module
     */
    void removeModule( Module module ) {
        modules.remove( module );
        // If the module we are removing is the active module, we need to set another one active
        if( getActiveModule() == module ) {
            setActiveModule( modules.size() == 0 ? null : (Module)modules.get( 0 ) );
        }
        // Notifiy listeners
        notifyModuleRemoved( new ModuleEvent( getPhetApplication(), module ) );
    }

    /**
     * Gets the index of the specified Module.
     *
     * @param m
     * @return the index of the specified Module.
     */
    int indexOf( Module m ) {
        return modules.indexOf( m );
    }

    /**
     * Removes all Modules in this ModuleManager and adds those specified in the array.
     *
     * @param modules
     */
    void setModules( Module[] modules ) {
        while( numModules() > 0 ) {
            Module module = moduleAt( 0 );
            removeModule( module );
        }
        addAllModules( modules );
    }

    /**
     * Adds all specified Modules.
     *
     * @param modules
     */
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

    /**
     * Sets the ith indexed Module to be the active one.
     *
     * @param i
     */
    void setActiveModule( int i ) {
        setActiveModule( moduleAt( i ) );
    }

    /**
     * Sets the specified module to be the active one.
     *
     * @param module
     */
    void setActiveModule( Module module ) {
        if( module == null ) {
            throw new RuntimeException( "Active module can't be null." );
        }
        if( activeModule != module ) {
            deactivateCurrentModule();
            activate( module );
            notifyActiveModuleChanged( new ModuleEvent( getPhetApplication(), module ) );
            verifyActiveState();
        }
    }

    /*
     * Prints an exception stack trace if the collection of modules 
     * being managed don't have the correct activation and clock states.
     * A valid state is:
     * - exactly one module should be active
     * - at most one clock should be running
     * - if there is a running clock, it must belong to the active module
     */
    private void verifyActiveState() {
        int numActiveModules = getNumActiveModules();
        int numClocksRunning = getNumClocksRunning();
        boolean clockShared=isClockShared();
        if ( numActiveModules != 1 ) {
            new IllegalStateException( "multiple modules are running: active modules=" + numActiveModules ).printStackTrace();
        }
        if ( numClocksRunning > 1 ) {
            new IllegalStateException( "multiple clocks are running: running clocks=" + numClocksRunning ).printStackTrace();
        }
        if ( numClocksRunning == 1 && !activeModule.getClock().isRunning() ) {
            new IllegalStateException( "a clock is running that does not belong to the active module" ).printStackTrace();
        }
        if (clockShared){
            new IllegalStateException( "Multiple modules are using the same clock instance." ).printStackTrace();
        }
    }

    private boolean isClockShared() {
        for( int i = 0; i < modules.size(); i++ ) {
            for( int k = 0; k < modules.size(); k++ ) {
                if( k != i ) {
                    IClock clock1 = ( (Module)modules.get( i ) ).getClock();
                    IClock clock2 = ( (Module)modules.get( k ) ).getClock();
                    if (clock1==clock2){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getNumActiveModules() {
        int count=0;
        for( int i = 0; i < modules.size(); i++ ) {
            if (((Module)modules.get( i )).isActive() ){
                count++;
            }
        }
        return count;
    }

    /*
     * Returns the number of unique running clock instances attached to modules in this ModuleManager.
     */
    private int getNumClocksRunning() {
        ArrayList runningClocks=new ArrayList( );
        for( int i = 0; i < modules.size(); i++ ) {
            IClock clock = ( (Module)modules.get( i ) ).getClock();
            if ( clock.isRunning() ){
                if (!runningClocks.contains( clock)){
                    runningClocks.add(clock);
                }
            }
        }
        return runningClocks.size();
    }

    void addModuleObserver( ModuleObserver observer ) {
        moduleObservers.add( observer );
    }

    private PhetApplication getPhetApplication() {
        return phetApplication;
    }

    private void activate( Module module ) {
        activeModule = module;
        if( module != null ) {
            module.activate();
            this.setActiveModule( module );
        }
    }

    void deactivateCurrentModule() {
        if( activeModule != null ) {
            activeModule.deactivate();
        }
    }

    private void notifyModuleAdded( ModuleEvent event ) {
        for( int i = 0; i < moduleObservers.size(); i++ ) {
            ModuleObserver moduleObserver = (ModuleObserver)moduleObservers.get( i );
            moduleObserver.moduleAdded( event );
        }
    }

    private void notifyActiveModuleChanged( ModuleEvent event ) {
        for( int i = 0; i < moduleObservers.size(); i++ ) {
            ModuleObserver moduleObserver = (ModuleObserver)moduleObservers.get( i );
            moduleObserver.activeModuleChanged( event );
        }
    }

    private void notifyModuleRemoved( ModuleEvent event ) {
        for( int i = 0; i < moduleObservers.size(); i++ ) {
            ModuleObserver moduleObserver = (ModuleObserver)moduleObservers.get( i );
            moduleObserver.moduleRemoved( event );
        }
    }
}
