package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.umd.cs.piccolo.PNode;

public class TrackingHelpNode extends HelpBalloon {
    private CCKSimulationPanel cckSimulationPanel;
    private CCKModule module;
    private PNode followedNode;
    private PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            updateFollow();
        }
    };

    private void updateFollow() {
        pointAt( followedNode.getGlobalFullBounds().getCenter2D() );
    }

    public TrackingHelpNode( CCKSimulationPanel cckSimulationPanel, CCKModule module, String text, Attachment attachmentPoint ) {
        super( cckSimulationPanel, text, attachmentPoint, 40 );
        this.cckSimulationPanel = cckSimulationPanel;
        this.module = module;
    }

    protected void setFollowedNode( PNode node ) {
        if ( this.followedNode != node ) {
            if ( this.followedNode != null ) {
                followedNode.removePropertyChangeListener( listener );
            }
            this.followedNode = node;
            followedNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
            updateFollow();
        }
    }

    protected CCKSimulationPanel getCckSimulationPanel() {
        return cckSimulationPanel;
    }
}