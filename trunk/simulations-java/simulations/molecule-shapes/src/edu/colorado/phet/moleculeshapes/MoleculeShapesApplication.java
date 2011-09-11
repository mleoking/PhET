// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;


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
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.dev.PerformanceFrame;
import edu.colorado.phet.moleculeshapes.jme.ColorRGBAPropertyControl;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.util.ColorPropertyControl;

/**
 * The main application for Molecule Shapes
 */
public class MoleculeShapesApplication extends PiccoloPhetApplication {

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

        developerMenu.add( new JSeparator() );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Allow drag movement behind the molecule center", MoleculeShapesProperties.allowDraggingBehind ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "\"Move\" mouse cursor on rotation", MoleculeShapesProperties.useRotationCursor ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Allow bond angles between lone pairs", MoleculeShapesProperties.allowAnglesBetweenLonePairs ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show colored bonds for real molecules", MoleculeShapesProperties.useColoredBonds ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Disable \"Show Bond Angles\" checkbox with less than 2 bonds", MoleculeShapesProperties.disableNAShowBondAngles ) );
        developerMenu.add( new JSeparator() );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show FPS", new Property<Boolean>( false ) {{
            addObserver( new SimpleObserver() {
                             public void update() {
                                 module.getApp().statistics.setDisplayFps( get() );
                             }
                         }, false );
        }} ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Show Statistics", new Property<Boolean>( false ) {{
            addObserver( new SimpleObserver() {
                             public void update() {
                                 module.getApp().statistics.setDisplayStatView( get() );
                             }
                         }, false );
        }} ) );
        developerMenu.add( new JSeparator() );
        developerMenu.add( new JMenuItem( "Color Options" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new JDialog( frame ) {{
                        setTitle( "Color Options" );
                        setResizable( false );

                        setContentPane( new JPanel() {{
                            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
                            add( new ColorRGBAPropertyControl( frame, "Background color: ", module.getApp().backgroundColor ) );
                            add( new ColorRGBAPropertyControl( frame, "Central atom color: ", MoleculeShapesConstants.COLOR_ATOM_CENTER ) );
                            add( new ColorRGBAPropertyControl( frame, "Radial atom color: ", MoleculeShapesConstants.COLOR_ATOM ) );
                            add( new ColorPropertyControl( frame, "Remove all foreground color: ", MoleculeShapesConstants.REMOVE_BUTTON_TEXT_COLOR ) );
                            add( new ColorPropertyControl( frame, "Remove all background color: ", MoleculeShapesConstants.REMOVE_BUTTON_BACKGROUND_COLOR ) );
                        }} );
                        pack();
                        SwingUtils.centerInParent( this );
                    }}.setVisible( true );
                }
            } );
        }} );
        developerMenu.add( new JMenuItem( "Performance Options" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new PerformanceFrame( module.getApp() );
                }
            } );
        }} );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        JmeUtils.initializeJME( args );

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, MoleculeShapesConstants.PROJECT_NAME, MoleculeShapesApplication.class );
    }
}
