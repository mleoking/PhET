// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ColorDialogMenuItem;
import edu.colorado.phet.sugarandsaltsolutions.macro.MacroModule;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroModule;
import edu.colorado.phet.sugarandsaltsolutions.water.WaterModule;

import static edu.colorado.phet.sugarandsaltsolutions.micro.MicroModule.SIZE_SCALE;

/**
 * Main application for PhET's "Sugar and Salt Solutions" simulation
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsApplication extends PiccoloPhetApplication {
    public static final Color WATER_COLOR = new Color( 179, 239, 243 );
    public static Random random = new Random();
    public static Property<Double> sizeScale = new Property<Double>( 1.0 );

    public SugarAndSaltSolutionsApplication( PhetApplicationConfig config ) {
        super( config );

        //Create a shared configuration for changing colors or state in all tabs
        final GlobalState globalState = new GlobalState( new SugarAndSaltSolutionsColorScheme(), config, getPhetFrame() );

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

        //Parse command line args for a directive like "-module 2" which will set that as the startup module (0-based indices)
        //This is done so that the developer can easily specify a starting tab for the simulation by changing the command line arguments
        if ( config.isDev() ) {
            int index = Arrays.asList( config.getCommandLineArgs() ).indexOf( "-module" );
            if ( index >= 0 && index + 1 < config.getCommandLineArgs().length ) {
                String nextArg = config.getCommandLineArgs()[index + 1];
                int module = Integer.parseInt( nextArg );
                setStartModule( moduleAt( module ) );
            }
        }

        //Add developer menus for changing the color of background and salt
        getPhetFrame().getDeveloperMenu().add( new ColorDialogMenuItem( getPhetFrame(), "Background Color...", globalState.colorScheme.backgroundColorSet.selectedColor ) );
        getPhetFrame().getDeveloperMenu().add( new ColorDialogMenuItem( getPhetFrame(), "Salt Color...", globalState.colorScheme.saltColor.selectedColor ) );

        getPhetFrame().addMenu( new OptionsMenu() {{addWhiteBackgroundCheckBoxMenuItem( globalState.colorScheme.whiteBackground );}} );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, SugarAndSaltSolutionsResources.NAME, SugarAndSaltSolutionsApplication.class );
    }
}