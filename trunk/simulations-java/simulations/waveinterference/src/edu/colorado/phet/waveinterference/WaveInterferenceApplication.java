/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 10:52:38 PM
 */

public class WaveInterferenceApplication extends PiccoloPhetApplication {
    private static String VERSION = "1.01";
    private static final String LOCALIZATION_BUNDLE_BASENAME = "waveinterference/localization/waveinterference-strings";

    public WaveInterferenceApplication( String[] args ) {
        super( args, WIStrings.getString( "waveinterference.name" ), WIStrings.getString( "waveinterference.description" ), VERSION, new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 50, 50 ) ) );
        WaveInterferenceMenu menu = new WaveInterferenceMenu();
        addModule( new WaterModule() );
        addModule( new SoundModule() );
        LightModule lightModule = new LightModule();
        addModule( lightModule );
        menu.add( new ColorizeCheckBoxMenuItem( lightModule ) );
        getPhetFrame().addMenu( menu );
        if( getModules().length > 1 ) {
            for( int i = 0; i < getModules().length; i++ ) {
                getModule( i ).setLogoPanelVisible( false );
            }
        }
    }

//    protected PhetFrame createPhetFrame() {
//        return new PhetFrameWorkaround( this );
//    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                SimStrings.getInstance().init( args, LOCALIZATION_BUNDLE_BASENAME );
                new WaveIntereferenceLookAndFeel().initLookAndFeel();
//        if( Arrays.asList( args ).contains( "-smooth" ) ) {
//            doSmooth();
//        }
                new WaveInterferenceApplication( args ).startApplication();
            }
        } );
    }

//    private static void doSmooth() {
//        try {
//            final String systemLookAndFeelClassName = SmoothLookAndFeelFactory.getSystemLookAndFeelClassName();
//            System.out.println( "systemLookAndFeelClassName = " + systemLookAndFeelClassName );
//            UIManager.setLookAndFeel( systemLookAndFeelClassName );
//        }
//        catch( ClassNotFoundException e ) {
//            e.printStackTrace();
//        }
//        catch( InstantiationException e ) {
//            e.printStackTrace();
//        }
//        catch( IllegalAccessException e ) {
//            e.printStackTrace();
//        }
//        catch( UnsupportedLookAndFeelException e ) {
//            e.printStackTrace();
//        }
//    }
}
