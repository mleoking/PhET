/*  */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 9:57:09 AM
 */

public class TheRampApplication extends PiccoloPhetApplication {
    private static final String VERSION = PhetApplicationConfig.getVersion( "the-ramp" ).formatForTitleBar();
    public static final double FORCE_LENGTH_SCALE = 0.1;//1.0;

    private RampModule simpleRampModule;
    private RampModule advancedFeatureModule;

    public TheRampApplication( String[] args, FrameSetup frameSetup ) {
        super( args, TheRampStrings.getString( "the.ramp" ), TheRampStrings.getString( "the.ramp.simulation" ),
               VERSION, frameSetup );
        simpleRampModule = new SimpleRampModule( getPhetFrame(), createClock() );
        advancedFeatureModule = new RampModule( getPhetFrame(), createClock() );
        setModules( new Module[]{simpleRampModule, advancedFeatureModule} );
    }

    private IClock createClock() {
        return new SwingClock( 30, 1.0 / 30.0 );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                TheRampStrings.init( args );
                PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
                phetLookAndFeel.initLookAndFeel();

                final FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 600 ) );
                final TheRampApplication application = new TheRampApplication( args, frameSetup );
                application.startApplication();
                application.simpleRampModule.getPhetPCanvas().requestFocus();
                application.simpleRampModule.applicationStarted();
            }
        } );

    }

}
