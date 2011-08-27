// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.jme.ColorRGBAPropertyControl;

import com.jme3.system.JmeSystem;
import com.jme3.system.Natives;

/**
 * The main application for Molecule Shapes
 */
public class MoleculeShapesApplication extends PiccoloPhetApplication {

    public static final Property<Boolean> allowDraggingBehind = new Property<Boolean>( true );
    public static final Property<Boolean> useRotationCursor = new Property<Boolean>( true );
    public static final Property<Boolean> showMolecularShapeName = new Property<Boolean>( false );
    public static final Property<Boolean> showElectronShapeName = new Property<Boolean>( false );
    public static final Property<Boolean> showBondAngles = new Property<Boolean>( false );

    private MoleculeShapesModule module;

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public MoleculeShapesApplication( PhetApplicationConfig config ) {
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

        module = new MoleculeShapesModule( parentFrame, Strings.MOLECULE__SHAPES__TITLE );
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

        developerMenu.add( new PropertyCheckBoxMenuItem( "Allow drag movement behind the molecule center", allowDraggingBehind ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "\"Move\" mouse cursor on rotation", useRotationCursor ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "(beta, slow) Show Bond Angles", showBondAngles ) );
        developerMenu.add( new JMenuItem( "Change background color" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new JDialog( frame ) {{
                        setTitle( "Background Color" );
                        setResizable( false );

                        setContentPane( new ColorRGBAPropertyControl( frame, "background color:", module.getApp().backgroundColor ) );
                        pack();
                        SwingUtils.centerInParent( this );
                    }}.setVisible( true );
                }
            } );
        }} );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show FPS", new Property<Boolean>( false ) {{
            addObserver( new SimpleObserver() {
                             public void update() {
                                 module.getApp().setDisplayFps( get() );
                             }
                         }, false );
        }} ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show Statistics", new Property<Boolean>( false ) {{
            addObserver( new SimpleObserver() {
                             public void update() {
                                 module.getApp().setDisplayStatView( get() );
                             }
                         }, false );
        }} ) );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        // don't spam the console output for every cylinder that we re-create (every frame)
        Logger.getLogger( "de.lessvoid" ).setLevel( Level.SEVERE );
        Logger.getLogger( "com.jme3" ).setLevel( Level.SEVERE );

        // since we are including the JME3 native lib dependencies in the JNLP, don't load if we are running online
        if ( System.getProperty( "javawebstart.version" ) != null ) {
            // see http://jmonkeyengine.org/wiki/doku.php/jme3:webstart
            JmeSystem.setLowPermissions( true );
        }
        else {
            // create a temporary directory to hold native libs
            final File tempDir = new File( System.getProperty( "java.io.tmpdir" ), "phet-jme3-libs" );
            tempDir.mkdirs();
            final String path = tempDir.getAbsolutePath();
            System.out.println( "Extracting native JME3 libraries to: " + path );
            Natives.setExtractionDir( path );
            tempDir.deleteOnExit();
        }

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, MoleculeShapesConstants.PROJECT_NAME, MoleculeShapesApplication.class );
    }
}
