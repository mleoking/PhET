// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.module.AbstractBuildAMoleculeModule;
import edu.colorado.phet.buildamolecule.module.CollectMultipleModule;
import edu.colorado.phet.buildamolecule.module.LargerMoleculesModule;
import edu.colorado.phet.buildamolecule.module.MakeMoleculeModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * The main application for this simulation.
 */
public class BuildAMoleculeApplication extends PiccoloPhetApplication {

    /**
     * Allows use of all molecules in the collection boxes
     */
    public static final Property<Boolean> allowGenerationWithAllMolecules = new Property<Boolean>( false );

    /**
     * Allows putting any molecule into a box when it has the same molecular formula
     */
    public static final Property<Boolean> allowCollectionBoxMatchingByMolecularFormula = new Property<Boolean>( false );

    /**
     * If true, resetting a kit doesn't pull atoms from the collection box, but instead creates new ones to fill the buckets
     */
    public static final Property<Boolean> resetKitIgnoresCollectionBoxes = new Property<Boolean>( false );

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public BuildAMoleculeApplication( PhetApplicationConfig config ) {
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

        Module makeMoleculeModule = new MakeMoleculeModule( parentFrame );
        addModule( makeMoleculeModule );

        Module collectMultipleModule = new CollectMultipleModule( parentFrame );
        addModule( collectMultipleModule );

        Module largerMolecules = new LargerMoleculesModule( parentFrame );
        addModule( largerMolecules );
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
        developerMenu.add( new JMenuItem( "Show Table of Molecules" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new MoleculeTableDialog( getPhetFrame() ).setVisible( true );
                }
            } );
        }} );
        developerMenu.add( new JMenuItem( "Regenerate model" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (AbstractBuildAMoleculeModule) getActiveModule() ).regenerateModelIfPossible();
                }
            } );
        }} );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Allow generation with all molecules", allowGenerationWithAllMolecules ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Allow all isomers of target molecule in boxes", allowCollectionBoxMatchingByMolecularFormula ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Reset kit leaves collection box alone (any molecule left in box will be permanent)", resetKitIgnoresCollectionBoxes ) );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /*
         * If you want to customize your application (look-&-feel, window size, etc)
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, BuildAMoleculeConstants.PROJECT_NAME, BuildAMoleculeApplication.class );
    }
}
