// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import java.awt.*;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ColorDialogMenuItem;
import edu.colorado.phet.sugarandsaltsolutions.macro.MacroModule;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroModule;
import edu.colorado.phet.sugarandsaltsolutions.water.WaterModule;

/**
 * Main application for PhET's "Sugar and Salt Solutions" simulation
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsApplication extends PiccoloPhetApplication {
    public static final Color WATER_COLOR = new Color( 179, 239, 243 );
    public static Random random = new Random();

    public SugarAndSaltSolutionsApplication( PhetApplicationConfig config ) {
        super( config );

        //Create a shared configuration for changing colors in all tabs
        final SugarAndSaltSolutionsColorScheme colorScheme = new SugarAndSaltSolutionsColorScheme();

        final GlobalState globalState = new GlobalState( colorScheme, config, getPhetFrame() );

        //Create the modules
        addModule( new MacroModule( colorScheme ) );
        addModule( new MicroModule( colorScheme ) );
        addModule( new WaterModule( globalState ) );

        if ( config.isDev() ) {
            setStartModule( moduleAt( 2 ) );
        }

        //Add developer menus for changing the color of background and salt
        getPhetFrame().getDeveloperMenu().add( new ColorDialogMenuItem( getPhetFrame(), "Background Color...", colorScheme.backgroundColor ) );
        getPhetFrame().getDeveloperMenu().add( new ColorDialogMenuItem( getPhetFrame(), "Salt Color...", colorScheme.saltColor ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, SugarAndSaltSolutionsResources.NAME, SugarAndSaltSolutionsApplication.class );
    }
}