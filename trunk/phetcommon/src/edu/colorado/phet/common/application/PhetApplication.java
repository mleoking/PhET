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

import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.SimStrings;

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
    
    /**
     * Initialize application localization.
     * 
     * @param args the commandline arguments that were passed to main
     * @param bundleName the base name of the resource bundle containing localized strings
     */
    public static void initLocalization( String[] args, String bundleName ) {
        // Get the default locale from property javaws.locale.
        String applicationLocale = System.getProperty( "javaws.locale" );
        if ( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            SimStrings.setLocale( new Locale( applicationLocale ) );
        }

        // Override default locale using "user.language=" command line argument.
        String argsKey = "user.language=";
        for ( int i = 0; i < args.length; i++ ) {
            if ( args[i].startsWith( argsKey ) ) {
                String locale = args[i].substring( argsKey.length(), args[i].length() );
                SimStrings.setLocale( new Locale( locale ) );
                break;
            }
        }

        // Initialize simulation strings using resource bundle for the locale.
        SimStrings.setStrings( bundleName );
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
}
