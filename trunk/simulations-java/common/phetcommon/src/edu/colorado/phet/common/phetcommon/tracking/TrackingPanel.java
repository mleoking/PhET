package edu.colorado.phet.common.phetcommon.tracking;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class TrackingPanel extends JPanel {
    private JLabel statusLabel = new JLabel();
    private JButton moreButton = new JButton( "More..." );
    private Tracker tracker;

    public TrackingPanel( Tracker tracker ) {
        this.tracker = tracker;
        add( statusLabel );
        add( moreButton );
        tracker.addListener( new Tracker.Listener() {
            public void stateChanged( Tracker tracker, Tracker.State oldState, Tracker.State newState ) {
                updateStatusLabel();
            }
        } );
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        statusLabel.setText( tracker.getStatus() );
    }

    public static void main( String[] args ) throws InterruptedException {
        JFrame frame = new JFrame();
        final PhetApplicationConfig config = new PhetApplicationConfig( args, new FrameSetup.CenteredWithSize( 1024, 768 ), new PhetResources( "nuclear-physics" ), "alpha-radiation" );
        Tracker tracker = new Tracker( new Trackable() {
            public TrackingInfo getTrackingInformation() {
                return new TrackingInfo( config );
            }
        } );
        frame.setContentPane( new TrackingPanel( tracker ) );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );

        Thread.sleep( 2000 );
        tracker.applicationStarted();
    }
}
