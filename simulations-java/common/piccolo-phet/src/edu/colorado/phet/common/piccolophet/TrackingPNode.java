package edu.colorado.phet.common.piccolophet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.tracking.Tracker;
import edu.colorado.phet.common.phetcommon.tracking.TrackingDialog;
import edu.colorado.phet.common.phetcommon.tracking.TrackingInfo;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

public class TrackingPNode extends PNode {
    private PText statusLabel = new PText();
    private JButton moreButton = new JButton( "More..." );
    private PSwing pswing = new PSwing( moreButton );
    private Tracker tracker;

    public TrackingPNode( Tracker tracker ) {
        this.tracker = tracker;
        addChild( statusLabel );
        addChild( pswing );
        moreButton.setFont( new PhetFont() );
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
                new TrackingDialog( TrackingPNode.this.tracker ).setVisible( true );
            }
        } );
        updateStatusLabel();
        relayout();
    }

    private void relayout() {
        statusLabel.setOffset( 0, pswing.getFullBounds().getCenterY() - statusLabel.getFullBounds().getHeight() / 2 );
        pswing.setOffset( statusLabel.getFullBounds().getWidth() + 2, 0 );
    }

    private void updateStatusLabel() {
        statusLabel.setText( tracker.getStatus() );
        relayout();
    }
}
