/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.forces1d.common_force1d.application;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import edu.colorado.phet.forces1d.common_force1d.model.clock.AbstractClock;
import edu.colorado.phet.forces1d.common_force1d.model.clock.ClockTickListener;
import edu.colorado.phet.forces1d.common_force1d.view.ApparatusPanel;
import edu.colorado.phet.forces1d.common_force1d.view.ApparatusPanel2;
import edu.colorado.phet.forces1d.common_force1d.view.PhetFrame;
import edu.colorado.phet.forces1d.common_force1d.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.forces1d.common_force1d.view.util.FrameSetup;

/**
 * The top-level class for all PhET applications.
 * <p/>
 * The prefered method of creating and starting a PhetApplication is shown here:
 * <code>
 * PhetApplication myApp = new PhetApplication( args, "Title", "Description", "Version,
 * clock, useClockControlPanel, frameSetup );
 * myApp.setModules( new Module[] { modA, modB };
 * myApp.startApplication();
 * </code>
 * <p/>
 * The application's PhetFrame is created by the constructor, and a new one will be created
 * if setFrameSetup() is called later.
 * <p/>
 * A FrameSetup can either be specified in the constructor
 * or later, in a call to setFrameSetup().
 * <p/>
 * If no initial module is specified, the module with index 0 in the array sent
 * to setModules() is used.
 *
 * @author ?
 * @version $Revision$
 */
public class PhetApplication {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------
    private static final String DEBUG_MENU_ARG = "-d";
    private static PhetApplication s_instance = null;

    public static PhetApplication instance() {
        return s_instance;
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private PhetFrame phetFrame;
    private ApplicationModel applicationModel;
    private ModuleManager moduleManager;
    private String title;
    private Module initialModule;
    private AbstractClock clock;
    private String description;
    private String version;
    private boolean useClockControlPanel;

    /**
     * @param args                 Command line args
     * @param title                Title that appears in the frame and the About dialog
     * @param description          Appears in the About dialog
     * @param version              Appears in the About dialog
     * @param clock                Simulation clock
     * @param useClockControlPanel Whether clock control panel appears at bottom of window
     * @param frameSetup           Defines the size and location of the frame
     */
    public PhetApplication( String[] args, String title, String description, String version, AbstractClock clock,
                            boolean useClockControlPanel, FrameSetup frameSetup ) {
        s_instance = this;
        this.moduleManager = new ModuleManager( this );
        this.title = title;
        this.clock = clock;
        this.description = description;
        this.version = version;
        this.useClockControlPanel = useClockControlPanel;
        if ( frameSetup != null ) {
            setFrameSetup( frameSetup );
        }

        // Handle command line arguments
        parseArgs( args );
    }

    /**
     * @param args                 Command line args
     * @param title                Title that appears in the frame and the About dialog
     * @param description          Appears in the About dialog
     * @param version              Appears in the About dialog
     * @param clock                Simulation clock
     * @param useClockControlPanel Whether clock control panel appears at bottom of window
     */
    public PhetApplication( String[] args, String title, String description, String version, AbstractClock clock,
                            boolean useClockControlPanel ) {
        this( args, title, description, version, clock, useClockControlPanel, null );
    }

    /**
     * @param descriptor
     * @deprecated, clients should pass in their String[] args.
     * @deprecated
     */
    public PhetApplication( ApplicationModel descriptor ) {
        this( descriptor, new String[0] );
    }

    /**
     * @param descriptor
     * @param args
     * @deprecated
     */
    public PhetApplication( ApplicationModel descriptor, String args[] ) {
        moduleManager = new ModuleManager( this );
        clock = descriptor.getClock();

        if ( descriptor.getModules() == null ) {
            throw new RuntimeException( "Module(s) not specified in ApplicationModel" );
        }
        if ( descriptor.getClock() == null ) {
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
        setInitialModule( descriptor.getInitialModule() );

        s_instance = this;

        // Handle command line arguments
        parseArgs( args );
    }

    /**
     * Processes command line arguments. May be extended by subclasses.
     *
     * @param args
     */
    protected void parseArgs( String[] args ) {
        for ( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if ( arg.equals( DEBUG_MENU_ARG ) ) {
                phetFrame.addDebugMenu();
            }
        }
    }

    /**
     * Starts the PhetApplication.
     * <p/>
     * Sets up the mechanism that sets the reference sizes of all ApparatusPanel2 instances.
     */
    public void startApplication() {
        if ( initialModule == null ) {
            throw new RuntimeException( "Initial module not specified." );
        }

        // Set up a mechanism that will set the reference sizes of all ApparatusPanel2 instances
        // after the PhetFrame has been set to its startup size. We have to do this with a strange
        // looking "inner listener". When the outer WindowAdapter gets called, the PhetFrame is
        // at the proper size, but the ApparatusPanel2 has not yet gotten its resize event.
        phetFrame.addWindowFocusListener( new WindowAdapter() {
            public void windowGainedFocus( WindowEvent e ) {
                for ( int i = 0; i < moduleManager.numModules(); i++ ) {
                    Module module = moduleManager.moduleAt( i );
                    ApparatusPanel panel = module.getApparatusPanel();
                    if ( panel instanceof ApparatusPanel2 ) {
                        final ApparatusPanel2 apparatusPanel = (ApparatusPanel2) panel;

                        // Add the listener to the apparatus panel that will tell it to set its
                        // reference size
                        apparatusPanel.setReferenceSize();
                    }
                }
                phetFrame.removeWindowFocusListener( this );
            }
        } );

        moduleManager.setActiveModule( initialModule );
        clock.start();
        phetFrame.setVisible( true );
    }

    /**
     * Sets the FrameSetup the application is to use, and initialized the PhetJComponent factory.
     *
     * @param frameSetup
     */
    public void setFrameSetup( FrameSetup frameSetup ) {
        phetFrame = new PhetFrame( this, title, clock, frameSetup, useClockControlPanel, moduleManager, description, version );
        PhetJComponent.init( phetFrame );
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    public AbstractClock getClock() {
        return clock;
    }

    public void addClockTickListener( ClockTickListener clockTickListener ) {
        clock.addClockTickListener( clockTickListener );
    }

    public void removeClockTickListener( ClockTickListener clockTickListener ) {
        clock.removeClockTickListener( clockTickListener );
    }

    //----------------------------------------------------------------
    // Module-related methods
    //----------------------------------------------------------------

    /**
     * Sets the modules in the application
     *
     * @param modules
     */
    public void setModules( Module[] modules ) {
        // Remove any modules that may currently be in the module manager
        while ( moduleManager.numModules() > 0 ) {
            Module module = moduleManager.moduleAt( 0 );
            moduleManager.removeModule( module );
        }
        // Add the new modules
        phetFrame.setModules( modules );
        moduleManager.addAllModules( modules );
        phetFrame.pack();

        // Set the default initial module
        setInitialModule( modules[0] );
    }

    /**
     * Specifies the module that will be activated when the application starts. If
     * this method is never called, the first module in the modules array is used.
     *
     * @param module
     */
    public void setInitialModule( Module module ) {
        this.initialModule = module;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ApplicationModel getApplicationModel() {
        return this.applicationModel;
    }

    public Module moduleAt( int i ) {
        return moduleManager.moduleAt( i );
    }

    public void setActiveModule( Module module ) {
        moduleManager.setActiveModule( module );
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

    public int numModules() {
        return moduleManager.numModules();
    }
}
