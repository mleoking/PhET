package edu.colorado.phet.movingman.motion.ramps;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.movingman.MovingManApplicationORIG;
import edu.colorado.phet.movingman.motion.MotionProjectLookAndFeel;
import edu.colorado.phet.movingman.MovingManApplication;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:23:32 PM
 */
public class Force1DMotionApplication extends PiccoloPhetApplication {
    
//    public Force1DMotionApplication( String[] args ) {
//        super( new PhetApplicationConfig( args, new FrameSetup.TopCenter( 1024, 768 ), new PhetResources( "moving-man" ), "mm-ramps" ) );
//        addModule( new Force1DMotionModule( new ConstantDtClock( (int) MovingManApplication.FRAME_DELAY_MILLIS, MovingManApplication.FRAME_DELAY_SEC ) ) );
//    }
//
//    public static void main( final String[] args ) {
//        SwingUtilities.invokeLater( new Runnable() {
//            public void run() {
//                SimStrings.getInstance().init( args, "forces-1d/localization/forces-1d-strings" );//todo: replace with resource loader
//                SimStrings.getInstance().init( args, "the-ramp/localization/the-ramp-strings" );//todo: replace with resource loader
//                PhetLookAndFeel laf = new MotionProjectLookAndFeel();
//                laf.initLookAndFeel();
//                SimStrings.getInstance().addStrings( MovingManApplicationORIG.localizedStringsPath );
//                new Force1DMotionApplication( args ).startApplication();
//            }
//        } );
//
//    }
    
    public Force1DMotionApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new Force1DMotionModule( new ConstantDtClock( (int) MovingManApplication.FRAME_DELAY_MILLIS, MovingManApplication.FRAME_DELAY_SEC ) ) );
    }
    
    public static void main( final String[] args ) {
        
        SimStrings.getInstance().init( args, "forces-1d/localization/forces-1d-strings" );//todo: replace with resource loader
        SimStrings.getInstance().init( args, "the-ramp/localization/the-ramp-strings" );//todo: replace with resource loader
        SimStrings.getInstance().addStrings( MovingManApplicationORIG.localizedStringsPath );
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new Force1DMotionApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, "moving-man", "mm-ramps" );
        appConfig.setLookAndFeel( new MotionProjectLookAndFeel() );
        appConfig.launchSim();
    }

}
