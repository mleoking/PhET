/**
 * Class: ModuleManager
 * Package: edu.colorado.phet.common.application
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.AbstractClock;

import java.util.ArrayList;

/**
 * Maintains a list of all modules in an application, and provides a mechanism for switching
 * which module is active.  (Only supports one active module.)
 */
public class ModuleManager implements ClockTickListener{
    private ArrayList modules = new ArrayList();
    private Module activeModule;
    ArrayList observers = new ArrayList();
    PhetApplication application;

    public ModuleManager( PhetApplication application ) {
        this.application = application;
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
    public static boolean moduleIsWellFormed( Module module ) {
        boolean result = true;
        result &= module.getModel() != null;
        result &= module.getApparatusPanel() != null;
        return result;
    }

    public void addModule( Module module, boolean isActive ) {

        // Check that the module is well-formed
        if (!moduleIsWellFormed(module )){
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

//    public ArrayList getModules() {
//        return modules;
//    }

    public void setActiveModule( int i ) {
        setActiveModule( moduleAt( i ) );
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

    public int indexOf( Module m ) {
        return modules.indexOf( m );
    }

    public void addAllModules( Module[] modules ) {
        for( int i = 0; i < modules.length; i++ ) {
            addModule( modules[i] );
        }
    }

    public void activateModuleOfClass( Class moduleClass ) {
        Module module = null;
        for( int i = 0; i < modules.size() && module == null; i++ ) {
            if( moduleClass.isInstance( modules.get( i ) ) ) {
                module = (Module)modules.get( i );
                this.setActiveModule( module );
            }
        }
    }
    /**Forwards clock ticks to the active module.*/
    public void clockTicked( AbstractClock c, double dt ) {
        getActiveModule().getModel().clockTicked(c,dt);
    }
}
