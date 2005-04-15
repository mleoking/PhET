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

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.util.DebugMenu;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.FrameSetup;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The top-level class for all PhET applications.
 * It contains a PhetFrame and ApplicationModel.
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
    // Instance data
    //----------------------------------------------------------------

    private String name;
    private String windowTitle;
    private String description;
    private String version;
    private FrameSetup frameSetup;
    private Module[] modules = new Module[0];
    private Module initialModule;
    private AbstractClock clock;
    boolean useClockControlPanel = true;

    private PhetFrame phetFrame;
    private ApplicationModel applicationModel;
    private ModuleManager moduleManager;

    //----------------------------------------------------------------
    // Constructors and initialization
    //----------------------------------------------------------------

    /**
     * @param args        Command line arguments
     * @param clock       The simulation clock
     * @param title
     * @param description
     * @param version
     */
    public PhetApplication( String args[], AbstractClock clock,
                            String title, String description, String version ) {
        this( args, clock, null, title, description, version );
    }

    /**
     * @param args        Command line arguments
     * @param clock       The simulation clock
     * @param frameSetup
     * @param title
     * @param description
     * @param version
     */
    public PhetApplication( String args[], AbstractClock clock, FrameSetup frameSetup,
                            String title, String description, String version ) {
        moduleManager = new ModuleManager( this );
        applicationModel = new ApplicationModel( title, description, version );
        applicationModel.setClock( clock );
        applicationModel.setFrameSetup( frameSetup );
        s_instance = this;

        try {
            phetFrame = new PhetFrame( this );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        parseArgs( args );
    }

    /**
     * @param descriptor
     * @deprecated
     */
    public PhetApplication( ApplicationModel descriptor ) {
        this( descriptor, null );
    }

    /**
     * @param descriptor
     * @param args
     * @deprecated
     */
    public PhetApplication( ApplicationModel descriptor, String args[] ) {
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

        // Handle command line arguments
        parseArgs( args );
    }

    /**
     * Processes command line arguments. May be extended by subclasses.
     *
     * @param args
     */
    protected void parseArgs( String[] args ) {
        for( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( DEBUG_MENU_ARG ) ) {
                phetFrame.addMenu( new DebugMenu( this ) );
            }
        }
    }

    /**
     * Starts the PhetApplication.
     */
    public void startApplication() {
        if( applicationModel.getInitialModule() == null ) {
            throw new RuntimeException( "Initial module not specified." );
        }

        if( frameSetup != null ) {
            frameSetup.initialize( getPhetFrame() );
        }
        moduleManager.setActiveModule( applicationModel.getInitialModule() );
        applicationModel.start();
        phetFrame.setVisible( true );

        // Set up a mechanism that will set the reference sizes of all ApparatusPanel2 instances
        // after the PhetFrame has been set to its startup size. We have to do this with a strange
        // looking "inner listener". When the outer WindowAdapter gets called, the PhetFrame is
        // at the proper size, but the ApparatusPanel2 has not yet gotten its resize event.
        phetFrame.addWindowFocusListener( new WindowAdapter() {
            public void windowGainedFocus( WindowEvent e ) {
                for( int i = 0; i < applicationModel.getModules().length; i++ ) {
                    Module module = (Module)applicationModel.getModules()[i];
                    ApparatusPanel panel = module.getApparatusPanel();
                    if( panel instanceof ApparatusPanel2 ) {
                        final ApparatusPanel2 apparatusPanel = (ApparatusPanel2)panel;

                        // Add the listener to the apparatus panel that will tell it to set its
                        // reference size
                        apparatusPanel.addComponentListener( new ComponentAdapter() {
                            public void componentResized( ComponentEvent e ) {
                                apparatusPanel.setReferenceSize();
                                apparatusPanel.removeComponentListener( this );
                            }
                        } );
                    }
                }
                phetFrame.removeWindowFocusListener( this );
            }
        } );
    }

    //----------------------------------------------------------------
    // Module management
    //----------------------------------------------------------------

    public void addModule( Module module ) {
        Module[] modules = applicationModel.getModules();
        ArrayList ml = new ArrayList();
        ml.addAll( Arrays.asList( modules ) );
        applicationModel.setModules( (Module[])ml.toArray( new Module[ml.size()] ) );
        moduleManager.addModule( module );
    }

    public void setInitialModule( Module module ) {
        applicationModel.setInitialModule( module );
    }

    public int numModules() {
        return moduleManager.numModules();
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


    //----------------------------------------------------------------
    // Setters and Getters
    //----------------------------------------------------------------

    public AbstractClock getClock() {
        return applicationModel.getClock();
    }

    public void setClock( AbstractClock clock ) {
        applicationModel.setClock( clock );
    }

    public void setFrameSetup( FrameSetup framesetup ) {
        applicationModel.setFrameSetup( framesetup );
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    // TODO: make this private, at least
    public ApplicationModel getApplicationModel() {
        return this.applicationModel;
    }

    public void addClockTickListener( ClockTickListener clockTickListener ) {
        applicationModel.getClock().addClockTickListener( clockTickListener );
    }

    public void removeClockTickListener( ClockTickListener clockTickListener ) {
        applicationModel.getClock().removeClockTickListener( clockTickListener );
    }

    public String getWindowTitle() {
        return applicationModel.getWindowTitle();
    }

    public String getDescription() {
        return applicationModel.getDescription();
    }

    public String getVersion() {
        return applicationModel.getVersion();
    }

    public String getName() {
        return applicationModel.getName();
    }

    public FrameSetup getFrameSetup() {
        return applicationModel.getFrameSetup();
    }

    public boolean getUseClockControlPanel() {
        return applicationModel.getUseClockControlPanel();
    }
}
