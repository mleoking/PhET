/**
 * Class: SoundApplication
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_sound.application.Module;
import edu.colorado.phet.common_sound.application.PhetApplication;


public class SoundApplication extends PhetApplication {


    public SoundApplication( String[] args ) {

        super( args, SimStrings.get( "SoundApplication.title" ),
               SimStrings.get( "SoundApplication.description" ),
               SoundConfig.VERSION,
               new ConstantDtClock( SoundConfig.s_waitTime, SoundConfig.s_timeStep ),
               true,
               new FrameSetup.CenteredWithSize( 900, 750 ) );

        // Set the look and feel to Metal, so the stopwatch dialog and the multi-line
        // tabs look right on the Mac
        try {
            UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" );
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }

        // Must not be resizable, because the performance tanks when you make the
        // window bigger. SHOULD BE FIXED!!!
        getPhetFrame().setResizable( false );

        // Set up the modules
        Module singleSourceModule = new SingleSourceListenModule( this );
        Module measureModule = new SingleSourceMeasureModule( this );
        Module twoSourceIntereferenceModule = new TwoSpeakerInterferenceModule( this );
        Module wallInterferenceModule = new WallInterferenceModule( this );
        Module evacuatedBoxModule = new SingleSourceWithBoxModule( this );
        this.setModules( new Module[]{singleSourceModule, measureModule,
                twoSourceIntereferenceModule, wallInterferenceModule,
                evacuatedBoxModule} );
        this.setInitialModule( singleSourceModule );

    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PhetLookAndFeel().initLookAndFeel();
                SimStrings.getInstance().addStrings( SoundConfig.localizedStringsPath );
                SimStrings.getInstance().addStrings( "sound/localization/phetcommon-strings" );

                PhetApplication app = new SoundApplication( args );
                app.getPhetFrame().setResizable( false );

                app.startApplication();
            }
        } );

    }
}
