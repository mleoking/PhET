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
import edu.colorado.phet.common.view.util.SimStrings;

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

    public PhetApplication( String args[], AbstractClock clock, String title, String description, String version ) {

        moduleManager = new ModuleManager( this );
        applicationModel = new ApplicationModel( title, description, version );
        applicationModel.setClock( clock );
        s_instance = this;

        try {
            phetFrame = new PhetFrame( this );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        parseArgs( args );
    }

    protected AbstractClock getClock() {
        return applicationModel.getClock();
    }

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

    public void setClock( AbstractClock clock ) {
        applicationModel.setClock( clock );
    }

    public void setFrameSetup( FrameSetup framesetup ) {
        applicationModel.setFrameSetup( framesetup );
    }

    public PhetApplication( ApplicationModel descriptor ) {
        this( descriptor, null );
    }

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


    private class ApplicationModel {
        private String name;
        private String windowTitle;
        private String description;
        private String version;
        private FrameSetup frameSetup;
        private Module[] modules = new Module[0];
        private Module initialModule;
        private AbstractClock clock;
        boolean useClockControlPanel = true;

        public ApplicationModel( String windowTitle, String description, String version ) {
            this( windowTitle, description, version, new FrameSetup.CenteredWithInsets( 200, 200 ) );
        }

        public ApplicationModel( String windowTitle, String description, String version, FrameSetup frameSetup ) {
            this.windowTitle = windowTitle;
            this.description = description;
            this.version = version;
            this.frameSetup = frameSetup;
            SimStrings.setStrings( "localization/CommonStrings" );
        }

        public ApplicationModel( String windowTitle, String description, String version, FrameSetup frameSetup, Module[] m, AbstractClock clock ) {
            this( windowTitle, description, version, frameSetup );
            setClock( clock );
            setModules( m );
            setInitialModule( m[0] );
        }

        public ApplicationModel( String windowTitle, String description, String version, FrameSetup frameSetup, Module m, AbstractClock clock ) {
            this( windowTitle, description, version, frameSetup );
            setClock( clock );
            setModule( m );
            setInitialModule( m );
        }

        public void setName( String name ) {
            this.name = name;
        }

        public void setModules( Module[] modules ) {
            this.modules = modules;
        }

        public void setModule( Module module ) {
            this.modules = new Module[]{module};
        }

        public void setInitialModule( Module initialModule ) {
            this.initialModule = initialModule;
        }

        public Module[] getModules() {
            return modules;
        }

        public Module getInitialModule() {
            return initialModule;
        }

        public AbstractClock getClock() {
            return clock;
        }

        public void setClock( AbstractClock clock ) {
            this.clock = clock;
        }

        public String getWindowTitle() {
            return windowTitle;
        }

        public String getDescription() {
            return description;
        }

        public String getVersion() {
            return version;
        }

        public FrameSetup getFrameSetup() {
            return frameSetup;
        }

        public void start() {
            clock.start();
        }

        public int numModules() {
            return modules.length;
        }

        public Module moduleAt( int i ) {
            return modules[i];
        }

        public boolean getUseClockControlPanel() {
            return useClockControlPanel;
        }

        public void setUseClockControlPanel( boolean useClockControlPanel ) {
            this.useClockControlPanel = useClockControlPanel;
        }

        public void setFrameSetup( FrameSetup frameSetup ) {
            this.frameSetup = frameSetup;
        }

        public void setFrameCenteredSize( int width, int height ) {
            setFrameSetup( new FrameSetup.CenteredWithSize( width, height ) );
        }

        public void setFrameCenteredInsets( int insetX, int insetY ) {
            setFrameSetup( new FrameSetup.CenteredWithInsets( insetX, insetY ) );
        }

        /**
         * The insets are a fallback-plan, when the frame is un-max-extented,
         * the frame will be centered with these specified insets.
         *
         * @param insetX
         * @param insetY
         */
        public void setFrameMaximized( int insetX, int insetY ) {
            setFrameSetup( new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( insetX, insetY ) ) );
        }

        public String getName() {
            return name;
        }
    }


}
