package edu.colorado.phet.movingman.motion.movingman;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSetNode;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.movingman.MovingManApplication;
import edu.colorado.phet.movingman.motion.MotionProjectLookAndFeel;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:38:34 AM
 */
public class MovingManMotionApplication extends PhetApplication {
    private GraphSetNode graphSetNode;
    private MovingManNode movingManNode;

    public MovingManMotionApplication( String[] args ) {
        super( new PhetApplicationConfig( args, new FrameSetup.TopCenter( 1024, 768 ), PhetResources.forProject( "moving-man" ), "mm-motion" ) );
        Module m = new MovingManMotionModule( new ConstantDtClock( 30, 1 ) );
        addModule( m );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                MotionProjectLookAndFeel.init();
                SimStrings.getInstance().addStrings( MovingManApplication.localizedStringsPath );
                new MovingManMotionApplication( args ).startApplication();
            }
        } );
    }
}
