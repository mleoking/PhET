// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.menu.ColorDialogMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.TeacherMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.sugarandsaltsolutions.macro.MacroModule;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroModule;
import edu.colorado.phet.sugarandsaltsolutions.water.WaterModule;

import static edu.colorado.phet.sugarandsaltsolutions.micro.MicroModule.SIZE_SCALE;
import static edu.colorado.phet.sugarandsaltsolutions.wetlab.SugarAndSaltSolutionsMicroApplication.SINGLE_MICRO_KIT;

/**
 * Main application for the PhET "Sugar and Salt Solutions" simulation
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsApplication extends PiccoloPhetApplication {

    //Global property for setting the size for atoms and molecules, since they are supposed to look and act smaller in the Micro tab than in real life
    //This was designed as a global property since propagating the scale through the object graphs on initialization was much more complex and confusing
    public static final Property<Double> sizeScale = new Property<Double>( 1.0 );

    public SugarAndSaltSolutionsApplication( PhetApplicationConfig config ) {
        super( config );

        //Create a shared configuration for changing colors or state in all tabs
        final GlobalState globalState = new GlobalState( new SugarAndSaltSolutionsColorScheme(), config, getPhetFrame(), Arrays.asList( config.getCommandLineArgs() ).contains( SINGLE_MICRO_KIT ) );

        //Create the modules
        addModule( new MacroModule( globalState ) );

        //Before creating the micro module, set the size scale for atoms, molecules and crystals to be 0.35, since they are supposed to look and act smaller in this tab
        //I investigated adding sizeScale arguments to usages, but it was making client code harder to read and maintain, since there are many atom types, molecule types and crystal types
        //Which all have to use the same sizeScale argument.  Instead, we are using this more "global" approach, and relying on module switching and these setters to make sure the scale is correct
        sizeScale.set( SIZE_SCALE );
        addModule( new MicroModule( globalState ) );

        //Restore the size scale to be 1.0 for the water module since no custom override is done there.
        sizeScale.set( 1.0 );
        addModule( new WaterModule( globalState ) );

        //Add developer menus for changing the color of background and salt
        getPhetFrame().getDeveloperMenu().add( new ColorDialogMenuItem( S3SimSharing.Components.backgroundColorMenuItem, getPhetFrame(), "Background Color...", globalState.colorScheme.backgroundColorSet.selectedColor ) );
        getPhetFrame().getDeveloperMenu().add( new ColorDialogMenuItem( S3SimSharing.Components.saltColorMenuItem, getPhetFrame(), "Salt Color...", globalState.colorScheme.saltColor.selectedColor ) );

        //Add a Teacher menu with an item to change the background to white for use on projectors in bright classrooms
        getPhetFrame().addMenu( new TeacherMenu() {{addWhiteBackgroundMenuItem( globalState.colorScheme.whiteBackground );}} );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, SugarAndSaltSolutionsResources.PROJECT_NAME, SugarAndSaltSolutionsApplication.class );
    }
}