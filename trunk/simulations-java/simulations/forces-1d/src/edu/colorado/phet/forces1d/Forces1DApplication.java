package edu.colorado.phet.forces1d;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;


public class Forces1DApplication extends PhetApplication {
    //todo: convert to proper use of PhetApplicationConfig for getting version
    static final String VERSION = new PhetResources( "forces-1d" ).getVersion().formatForTitleBar();
    public static Color FORCES_1D_BACKGROUND_COLOR = new Color( 200, 240, 200 );

    public Forces1DApplication( Forces1DPhetApplicationConfig config ) {
        super( config );

        IClock clock = new ConstantDtClock( 30, 1 );

        final Forces1DModule module;
        try {
            module = new Forces1DModule( clock, FORCES_1D_BACKGROUND_COLOR );

            Module[] m = new Module[]{module};
            setModules( m );

            JMenu options = new JMenu( Force1DResources.get( "Force1DModule.options" ) );
            JMenuItem item = new JMenuItem( Force1DResources.get( "Force1DModule.backgroundColor" ) );
            item.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.showColorDialog();
                }
            } );
            options.add( item );

            getPhetFrame().addMenu( options );

            if ( PhetUtilities.isMacintosh() ) {//max extent fails on mac + java 1.4
                new FrameSetup.CenteredWithInsets( 50, 50 ).initialize( getPhetFrame() );
            }
            module.setPhetFrame( getPhetFrame() );
            Forces1DModule.setup( module );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void main( final String[] args ) throws IOException {
        new PhetApplicationLauncher().launchSim( new Forces1DPhetApplicationConfig( args ), Forces1DApplication.class );
    }

    private static class Forces1DPhetApplicationConfig extends PhetApplicationConfig {
        public Forces1DPhetApplicationConfig( String[] args ) {
            super( args, "forces-1d" );
            setFrameSetup( new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 200, 200 ) ) );

            PhetLookAndFeel lookAndFeel = new PhetLookAndFeel();
            lookAndFeel.setBackgroundColor( FORCES_1D_BACKGROUND_COLOR );
            setLookAndFeel( lookAndFeel );
        }
    }
}