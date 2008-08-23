package edu.colorado.phet.forces1d;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.forces1d.common_force1d.application.Module;
import edu.colorado.phet.forces1d.common_force1d.application.PhetApplication;
import edu.colorado.phet.forces1d.common_force1d.model.clock.AbstractClock;
import edu.colorado.phet.forces1d.common_force1d.model.clock.SwingTimerClock;
import edu.colorado.phet.forces1d.common_force1d.view.PhetLookAndFeel;
import edu.colorado.phet.forces1d.common_force1d.view.util.FrameSetup;

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