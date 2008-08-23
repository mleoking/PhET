/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.phetcommon.application;

import java.beans.XMLEncoder;
import java.io.File;
import java.util.ArrayList;

import edu.colorado.phet.forces1d.phetcommon.util.EventChannel;

/**
 * ModuleManager
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleManager {

    public static boolean USE_GZIP_STREAMS = true;

    private ArrayList modules = new ArrayList();
    private Module activeModule;
    private EventChannel moduleObserversChannel = new EventChannel( ModuleObserver.class );
    private ModuleObserver moduleObserverProxy = (ModuleObserver) moduleObserversChannel.getListenerProxy();
    private PhetApplication phetApplication;

    public ModuleManager() {
    }

    public ModuleManager( PhetApplication phetApplication ) {
        this.phetApplication = phetApplication;
    }

    public Module moduleAt( int i ) {
        return (Module) modules.get( i );
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
        if ( !moduleIsWellFormed( module ) ) {
            throw new RuntimeException( "Module is missing something." );
        }

        modules.add( module );
        if ( isActive ) {
            setActiveModule( module );
        }
        moduleObserverProxy.moduleAdded( new ModuleEvent( this, module ) );
    }

    public void removeModule( Module module ) {
        modules.remove( module );

        // If the module we are removing is the active module, we need to
        // set another one active
        if ( getActiveModule() == module ) {
            setActiveModule( (Module) modules.get( 0 ) );
        }
        // Notifiy listeners
        moduleObserverProxy.moduleRemoved( new ModuleEvent( this, module ) );
    }

    public void setActiveModule( int i ) {
        setActiveModule( moduleAt( i ) );
    }

    public void setActiveModule( Module module ) {
        if ( activeModule != module ) {
            forceSetActiveModule( module );
        }
    }

    private void forceSetActiveModule( Module module ) {
        deactivate();
        activate( module );
        moduleObserverProxy.activeModuleChanged( new ModuleEvent( this, module ) );
    }

    private void activate( Module module ) {
        activeModule = module;
        module.activate( phetApplication );
    }

    private void deactivate() {
        if ( activeModule != null ) {
            activeModule.deactivate( phetApplication );
        }
    }

    public void addModuleObserver( ModuleObserver observer ) {
        moduleObserversChannel.addListener( observer );
    }

    public int indexOf( Module m ) {
        return modules.indexOf( m );
    }

    public void addAllModules( Module[] modules ) {
        for ( int i = 0; i < modules.length; i++ ) {
            addModule( modules[i] );
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Save/restore methods
    //
    public void saveStateToConsole() {
        for ( int i = 0; i < modules.size(); i++ ) {
            Module module = (Module) modules.get( i );
            XMLEncoder encoder = new XMLEncoder( System.out );
            encoder.writeObject( module );
            encoder.close();
        }
    }

    /**
     * Returns the an array of the modules the module manager manages
     *
     * @return
     */
    public Module[] getModules() {
        Module[] moduleArray = new Module[this.modules.size()];
        for ( int i = 0; i < modules.size(); i++ ) {
            Module module = (Module) modules.get( i );
            moduleArray[i] = module;
        }
        return moduleArray;
    }

    /**
     * File filter for *.pst files
     */
    private class PstFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept( File file ) {
            String filename = file.getName();
            return filename.endsWith( ".pst" ) || file.isDirectory();
        }

        public String getDescription() {
            return "*.pst";
        }
    }
}
