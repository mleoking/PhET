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

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.MultiMap;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import java.util.ArrayList;
import java.util.List;
import java.beans.*;
import java.io.*;
import java.awt.*;

/**
 * ModuleManager
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleManager {
//    private class ModuleManager {
    private ArrayList modules = new ArrayList();
    private Module activeModule;
    private ArrayList observers = new ArrayList();
    private PhetApplication phetApplication;

    public ModuleManager() {
    }

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
            PhetApplication.ModuleObserver moduleObserver = (PhetApplication.ModuleObserver)observers.get( i );
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
            PhetApplication.ModuleObserver moduleObserver = (PhetApplication.ModuleObserver)observers.get( i );
            moduleObserver.activeModuleChanged( module );
        }
    }

    public void addModuleObserver( PhetApplication.ModuleObserver observer ) {
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

    public void saveState( String fileName ) {

//        MultiMap multiMap = new MultiMap();
//        multiMap.put(new Integer(1), "ONE");
//        multiMap.put(new Integer(5), "FIVE");
//        multiMap.put(new Integer(3), "THREE");
//        System.out.println("save = " + multiMap);
//        XMLEncoder e = null;
//        try {
//            e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fileName)));
//        } catch (FileNotFoundException e1) {
//            e1.printStackTrace();
//        }
//        multiMap = getActiveModule().getApparatusPanel().getGraphic().getGraphicMap();
//        e = new XMLEncoder( System.out );
//        e.writeObject(multiMap);
//        e.close();


        XMLEncoder encoder = null;
        try {
//            encoder = new XMLEncoder(System.out);

            // Prevent the component for a PhetGraphic from being persisted for now. This keeps
            // ApparatusPanel from being persisted, for now.
            BeanInfo info = Introspector.getBeanInfo( PhetImageGraphic.class );
            PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            for( int i = 0; i < propertyDescriptors.length; i++ ) {
                PropertyDescriptor pd = propertyDescriptors[i];
                if( pd.getName().equals( "image" ) ) {
                    pd.setValue( "transient", Boolean.TRUE );
                }
            }
            encoder = new XMLEncoder( new BufferedOutputStream( new FileOutputStream( fileName ) ) );
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
        Module module = getActiveModule();
        StateDescriptor sd = module.getStateDescriptor();
        encoder.writeObject( sd );
        encoder.close();
    }

    public void restoreState( String fileName ) {
        XMLDecoder decoder = null;
        try {
            decoder = new XMLDecoder( new BufferedInputStream( new FileInputStream( fileName ) ) );
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }

        // Read in the StateDescriptor
        StateDescriptor sd = (StateDescriptor)decoder.readObject();
        decoder.setExceptionListener( new ExceptionListener() {
            public void exceptionThrown( Exception exception ) {
                exception.printStackTrace();
            }
        } );

        // Find the module that is of the same class as the one that we're
        // restoring. Set it to be the active module, and tell it to
        // restore itself from the saved state
        for( int i = 0; i < modules.size(); i++ ) {
            Module module = (Module)modules.get( i );
            if( module.getClass().getName().equals( sd.getModuleClassName() ) ) {
                setActiveModule( module );
                module.restoreState( sd );
            }
        }
    }


}
