// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;

import static edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel.backgroundColor;

/**
 * Control panel that allows the user to change the track friction and stickiness (roller-coaster mode).
 *
 * @author Sam Reid
 */
public class TrackControlPanel extends ControlPanelNode {

    public TrackControlPanel( final EnergySkateParkBasicsModule module ) {
        super( new TrackControlContentPane( module ), backgroundColor );
    }

    public static class TrackControlContentPane extends VBox {
        public TrackControlContentPane( final EnergySkateParkBasicsModule module ) {
            super( 10,

                   //Control box for track friction
                   new VBox( 10,
                             new PhetPText( EnergySkateParkResources.getString( "controls.show-friction" ), EnergySkateParkBasicsModule.TITLE_FONT ),
                             new OnOffPanel( module.frictionEnabled ),
                             new TrackFrictionSlider( module ) ),

                   //vertical space
                   new PhetPPath( new Rectangle2D.Double( 0, 0, 1, 10 ) ) {{
                       setStroke( null );
                   }},

                   //Control box for stickiness
                   new VBox( 0,
                             new PhetPText( EnergySkateParkResources.getString( "stickToTrack" ), EnergySkateParkBasicsModule.TITLE_FONT ),
                             new OnOffPanel( module.stickToTrack ) ) );
        }
    }
}