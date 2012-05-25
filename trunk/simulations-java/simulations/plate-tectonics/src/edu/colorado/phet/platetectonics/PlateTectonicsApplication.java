// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.platetectonics;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane.TabbedModule;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLPhetApplication;
import edu.colorado.phet.lwjglphet.StartupUtils;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.dev.PerformanceFrame;
import edu.colorado.phet.platetectonics.modules.CrustTab;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;

/**
 * Main simulation entry point
 */
public class PlateTectonicsApplication extends LWJGLPhetApplication {

    public static final Property<Boolean> showFPSMeter = new Property<Boolean>( false );
    public static final Property<Boolean> showDebuggingItems = new Property<Boolean>( false );

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public PlateTectonicsApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {

        final Frame parentFrame = getPhetFrame();

        final LWJGLCanvas canvas = LWJGLCanvas.getCanvasInstance();

        addModule( new TabbedModule( canvas ) {{
            addTab( new CrustTab( canvas ) );
            addTab( new PlateMotionTab( canvas ) );
        }} );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        // Create main frame.
        final PhetFrame frame = getPhetFrame();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu

        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...

        developerMenu.add( new JCheckBoxMenuItem( "Show Frames Per Second" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    final boolean show = isSelected();
                    LWJGLUtils.invoke( new Runnable() {
                        public void run() {
                            showFPSMeter.set( show );
                        }
                    } );
                }
            } );
        }} );

        developerMenu.add( new JCheckBoxMenuItem( "Show Debugging Items" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    final boolean show = isSelected();
                    LWJGLUtils.invoke( new Runnable() {
                        public void run() {
                            showDebuggingItems.set( show );
                        }
                    } );
                }
            } );
        }} );

        developerMenu.add( new JSeparator() );

        developerMenu.add( new JMenuItem( "Performance Options" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new PerformanceFrame();
                }
            } );
        }} );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        try {
            StartupUtils.setupLibraries();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, PlateTectonicsResources.PROJECT_NAME, PlateTectonicsApplication.class );
    }
}