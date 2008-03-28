/*  */
package edu.colorado.phet.waveinterference;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 10:52:38 PM
 */

public class WaveInterferenceApplication extends PiccoloPhetApplication {
    private static String VERSION = PhetApplicationConfig.getVersion( "wave-interference" ).formatForTitleBar();
    private static final String LOCALIZATION_BUNDLE_BASENAME = "wave-interference/localization/wave-interference-strings";

    public WaveInterferenceApplication( String[] args ) {
        super( args, WIStrings.getString( "wave-interference.name" ), WIStrings.getString( "wave-interference.description" ), VERSION, new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 50, 50 ) ) );
        WaveInterferenceMenu menu = new WaveInterferenceMenu();
        addModule( new WaterModule() );
        addModule( new SoundModule() );
        LightModule lightModule = new LightModule();
        addModule( lightModule );
        menu.add( new ColorizeCheckBoxMenuItem( lightModule ) );
        getPhetFrame().addMenu( menu );
        if ( getModules().length > 1 ) {
            for ( int i = 0; i < getModules().length; i++ ) {
                getModule( i ).setLogoPanelVisible( false );
            }
        }
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                SimStrings.getInstance().init( args, LOCALIZATION_BUNDLE_BASENAME );
                new WaveIntereferenceLookAndFeel().initLookAndFeel();
                new WaveInterferenceApplication( args ).startApplication();
            }
        } );
    }

}
