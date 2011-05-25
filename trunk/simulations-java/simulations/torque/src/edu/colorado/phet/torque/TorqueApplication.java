// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque;

import java.awt.*;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ColorDialogMenuItem;
import edu.colorado.phet.sugarandsaltsolutions.intro.IntroModule;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.IntroModel;
import edu.colorado.phet.sugarandsaltsolutions.intro.view.SeparateRemoveSaltSugarButtons;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroscopicModule;
import edu.colorado.phet.torque.game.TorqueGameModule;
import edu.colorado.phet.torque.teetertotter.TeeterTotterTorqueModule;
import edu.umd.cs.piccolo.PNode;

/**
 * Main application for PhET's torque simulation
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class TorqueApplication extends PiccoloPhetApplication {
    public static final String NAME = "torque";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    public TorqueApplication( PhetApplicationConfig config ) {
        super( config );

        //Create the modules
        addModule( new TeeterTotterTorqueModule() );
        addModule( new TorqueGameModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, TorqueApplication.class );
    }
}