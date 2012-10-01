// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.forcesandmotionbasics.motion.MotionModule;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarModule;

/**
 * PhET Application for "Forces and Motion: Basics", which has 3 tabs: "Tug of War", "Motion" and "Friction"
 *
 * @author Sam Reid
 */
public class ForcesAndMotionBasicsApplication extends PiccoloPhetApplication {

    public static final Color BROWN = new Color( 197, 154, 91 );
    public static final Color TOOLBOX_COLOR = new Color( 231, 232, 233 );

    public ForcesAndMotionBasicsApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new TugOfWarModule() );
        addModule( new MotionModule( "Motion", false ) );
        addModule( new MotionModule( "Friction", true ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "forces-and-motion-basics", ForcesAndMotionBasicsApplication.class );
    }
}