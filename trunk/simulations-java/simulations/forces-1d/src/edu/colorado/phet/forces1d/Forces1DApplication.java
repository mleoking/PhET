package edu.colorado.phet.forces1d;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common_force1d.application.Module;
import edu.colorado.phet.common_force1d.application.PhetApplication;
import edu.colorado.phet.common_force1d.model.BaseModel;
import edu.colorado.phet.common_force1d.model.clock.AbstractClock;
import edu.colorado.phet.common_force1d.model.clock.ClockTickEvent;
import edu.colorado.phet.common_force1d.model.clock.SwingTimerClock;
import edu.colorado.phet.common_force1d.util.QuickTimer;
import edu.colorado.phet.common_force1d.view.PhetFrame;
import edu.colorado.phet.common_force1d.view.PhetLookAndFeel;
import edu.colorado.phet.common_force1d.view.util.FrameSetup;
import edu.colorado.phet.forces1d.common.ColorDialog;
import edu.colorado.phet.forces1d.common.plotdevice.DefaultPlaybackPanel;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.view.Force1DLookAndFeel;
import edu.colorado.phet.forces1d.view.Force1DPanel;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:06:43 PM
 */
public class Forces1DApplication {
    static final String VERSION = PhetApplicationConfig.getVersion( "forces-1d" ).formatForTitleBar();
    public static void main( final String[] args ) throws IOException {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    runMain( args );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private static void runMain( String[] args ) throws IOException {
//        Force1DResources.getInstance().init( args, LOCALIZATION_BUNDLE_BASENAME );
//        Force1DResources.getInstance().addStrings( "forces-1d/localization/phetcommon-strings" );
        PhetLookAndFeel.setLookAndFeel();
        PhetLookAndFeel lookAndFeel = new PhetLookAndFeel();
        lookAndFeel.apply();

        AbstractClock clock = new SwingTimerClock( 1, 30 );
        FrameSetup frameSetup = ( new FrameSetup.CenteredWithInsets( 200, 200 ) );

        String version = VERSION;
        final PhetApplication phetApplication = new PhetApplication( args, Force1DResources.get( "Force1DModule.title" ) + " (" + version + ")",
                                                                     Force1DResources.get( "Force1DModule.description" ), version, clock, false, frameSetup );

        final Forces1DModule module = new Forces1DModule( clock, lookAndFeel );
        Module[] m = new Module[]{module};
        phetApplication.setModules( m );

        JMenu options = new JMenu( Force1DResources.get( "Force1DModule.options" ) );
        JMenuItem item = new JMenuItem( Force1DResources.get( "Force1DModule.backgroundColor" ) );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.showColorDialog();
            }
        } );
        options.add( item );

        phetApplication.getPhetFrame().addMenu( options );
        phetApplication.startApplication();

        new FrameSetup.MaxExtent().initialize( phetApplication.getPhetFrame() );
        if ( PhetUtilities.isMacintosh() ) {//max extent fails on mac + java 1.4
            new FrameSetup.CenteredWithInsets( 50, 50 ).initialize( phetApplication.getPhetFrame() );
        }
        module.setPhetFrame( phetApplication.getPhetFrame() );
        Forces1DModule.setup( module );
    }
}