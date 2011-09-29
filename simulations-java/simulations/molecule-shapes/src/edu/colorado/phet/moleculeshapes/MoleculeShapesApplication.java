// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.jmephet.JMEPhetApplication;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.dev.DeveloperOptions;
import edu.colorado.phet.moleculeshapes.util.ColorProfile;

/**
 * The main application for Molecule Shapes
 */
public class MoleculeShapesApplication extends JMEPhetApplication {

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
//        addModule( new MoleculeShapesModule( parentFrame, "Test Module" ) );
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

        JMenu teachersMenu = new JMenu( "Teacher" ); // TODO: i18n, in common?

        // color profiles
        ButtonGroup colorProfileGroup = new ButtonGroup();
        for ( final ColorProfile<MoleculeShapesColor> profile : MoleculeShapesColor.PROFILES ) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem( profile.getName() ) {{
                setSelected( profile == MoleculeShapesColor.DEFAULT );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        profile.apply( MoleculeShapesColor.handler );
                    }
                } );
            }};
            colorProfileGroup.add( item );
            teachersMenu.add( item );
        }

        frame.addMenu( teachersMenu );

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...

        DeveloperOptions.addDeveloperOptions( developerMenu, frame, module );
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
        new PhetApplicationLauncher().launchSim( args, MoleculeShapesConstants.PROJECT_NAME, MoleculeShapesApplication.class );
    }
}
