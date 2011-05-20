// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import java.awt.*;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ColorDialogMenuItem;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroscopicModule;

/**
 * Main application for PhET's "Sugar and Salt Solutions" simulation
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsApplication extends PiccoloPhetApplication {
    private static final String NAME = "sugar-and-salt-solutions";
    public static final PhetResources RESOURCES = new PhetResources( NAME );
    public static final Color WATER_COLOR = new Color( 179, 239, 243 );
    public static Random random = new Random();

    public SugarAndSaltSolutionsApplication( PhetApplicationConfig config ) {
        super( config );

        //Create a shared configuration for changing colors in all tabs
        final SugarAndSaltSolutionsColorScheme configuration = new SugarAndSaltSolutionsColorScheme();

        //Create the modules
//        addModule( new IntroModule( configuration ) );
        addModule( new MicroscopicModule( configuration ) );

        //Add developer menus for changing the color of background and salt
        getPhetFrame().getDeveloperMenu().add( new ColorDialogMenuItem( getPhetFrame(), "Background Color...", configuration.backgroundColor ) );
        getPhetFrame().getDeveloperMenu().add( new ColorDialogMenuItem( getPhetFrame(), "Salt Color...", configuration.saltColor ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, SugarAndSaltSolutionsApplication.class );
    }
}