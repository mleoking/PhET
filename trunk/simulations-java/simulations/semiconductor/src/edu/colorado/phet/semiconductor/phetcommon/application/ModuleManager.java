/**
 * Class: ModuleManager
 * Package: edu.colorado.phet.common.application
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.application;

import edu.colorado.phet.semiconductor.phetcommon.model.clock.AbstractClock;
import edu.colorado.phet.semiconductor.phetcommon.model.clock.ClockTickListener;

import java.util.ArrayList;

/**
 * Maintains a list of all modules in an application, and provides a mechanism for switching
 * which module is active.  (Only supports one active module.)
 */
public class ModuleManager implements ClockTickListener {
    private ArrayList modules = new ArrayList();
    private Module activeModule;
    ArrayList observers = new ArrayList();
    PhetApplication application;

    public ModuleManager( PhetApplication application ) {
        this.application = application;
    }

    public Module getActiveModule() {
        return activeModule;
    }

    public void addModule( Module module ) {
        addModule( module, false );
    }

    public static boolean moduleIsWellFormed( Module module ) {
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

    public void setActiveModule( Module module ) {
        if( activeModule != module ) {
            if( activeModule != null ) {
                activeModule.deactivate( application );
            }
            activeModule = module;
            module.activate( application );
        }
        for( int i = 0; i < observers.size(); i++ ) {
            ModuleObserver moduleObserver = (ModuleObserver)observers.get( i );
            moduleObserver.activeModuleChanged( module );
        }
    }

    public void addModuleObserver( ModuleObserver observer ) {
        observers.add( observer );
    }

    public void addAllModules( Module[] modules ) {
        for( int i = 0; i < modules.length; i++ ) {
            addModule( modules[i] );
        }
    }

    /**
     * Forwards clock ticks to the active module.
     */
    public void clockTicked( AbstractClock c, double dt ) {
        getActiveModule().getModel().clockTicked( c, dt );
    }
}
