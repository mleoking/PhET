// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.platetectonics;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane.TabbedModule;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.JMEPhetApplication;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.platetectonics.modules.CrustTab;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.view.PlateTectonicsJMEApplication;

import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;

public class PlateTectonicsApplication extends JMEPhetApplication {

    private CrustTab singlePlateTab;
    private PlateMotionTab doublePlateTab;
    private TabbedModule module;

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

        module = new JMEModule( parentFrame, new Function1<Frame, PhetJMEApplication>() {
            public PhetJMEApplication apply( Frame frame ) {
                return new PlateTectonicsJMEApplication( parentFrame );
            }
        } ) {{
            addTab( singlePlateTab = new CrustTab( parentFrame ) );
            addTab( doublePlateTab = new PlateMotionTab( parentFrame ) );
        }};
        addModule( module );
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
                            PlateTectonicsTab activeModule = (PlateTectonicsTab) module.selectedTab.get();
                            Camera cam = activeModule.getDebugCamera();
                            if ( cam != null ) {
                                FlyByCamera flyCam = new FlyByCamera( cam );
                                flyCam.setMoveSpeed( 400f );
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
        JMEUtils.frameRate.set( 10000 ); // unlimited, for testing TODO: remove after dev
        JMEUtils.initializeJME( args );

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, PlateTectonicsResources.NAME, PlateTectonicsApplication.class );
    }
}