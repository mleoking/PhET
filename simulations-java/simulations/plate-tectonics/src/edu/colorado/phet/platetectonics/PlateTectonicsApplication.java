// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.platetectonics;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.jmephet.JMEPhetApplication;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.platetectonics.modules.CrustModule;
import edu.colorado.phet.platetectonics.modules.PlateMotionModule;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;

import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;

public class PlateTectonicsApplication extends JMEPhetApplication {

    private CrustModule singlePlateModule;
    private PlateMotionModule doublePlateModule;

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

        Frame parentFrame = getPhetFrame();

        addModule( singlePlateModule = new CrustModule( parentFrame ) );
        addModule( doublePlateModule = new PlateMotionModule( parentFrame ) );
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

        developerMenu.add( new JSeparator() );

        developerMenu.add( new PropertyCheckBoxMenuItem( "Show FPS", new Property<Boolean>( false ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    JMEUtils.getApplication().statistics.setDisplayFps( get() );
                }
            }, false );
        }} ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show Statistics", new Property<Boolean>( false ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    JMEUtils.getApplication().statistics.setDisplayStatView( get() );
                }
            }, false );
        }} ) );
        developerMenu.add( new JMenuItem( "Activate fly-by camera" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    JMEUtils.invoke( new Runnable() {
                        public void run() {
                            PlateTectonicsModule activeModule = (PlateTectonicsModule) getActiveModule();
                            Camera cam = activeModule.getDebugCamera();
                            if ( cam != null ) {
                                FlyByCamera flyCam = new FlyByCamera( cam );
                                flyCam.setMoveSpeed( 100f );
                                flyCam.registerWithInput( JMEUtils.getApplication().getInputManager() );
                            }
                        }
                    } );
                }
            } );
        }} );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        JMEUtils.initializeJME( args );

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, PlateTectonicsResources.NAME, PlateTectonicsApplication.class );
    }
}