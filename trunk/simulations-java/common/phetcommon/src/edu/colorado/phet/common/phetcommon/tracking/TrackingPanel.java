package edu.colorado.phet.common.phetcommon.tracking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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

            public void trackingInfoChanged( TrackingInfo trackingInformation ) {
            }

            public void trackingFailed( IOException trackingException ) {
            }
        } );
        moreButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new TrackingDialog( TrackingPanel.this.tracker ).setVisible( true );
            }
        } );
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        statusLabel.setText( tracker.getStatus() );
    }

    public static void main( String[] args ) throws InterruptedException {
        JFrame frame = new JFrame();
        Tracker tracker = createTestTracker( args );
        frame.setContentPane( new TrackingPanel( tracker ) );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );

        Thread.sleep( 2000 );
        tracker.startTracking();
    }

    public static Tracker createTestTracker( String[] args ) {
        final PhetApplicationConfig config = new PhetApplicationConfig( args, new FrameSetup.CenteredWithSize( 1024, 768 ), new PhetResources( "nuclear-physics" ), "alpha-radiation" );
        return new Tracker( new Trackable() {
            public TrackingInfo getTrackingInformation() {
                return new TrackingInfo( config );
            }
        } );
    }
}
