// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.MatchChallenge;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class view for all challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ChallengeNode extends PhetPNode {

    public ChallengeNode( MatchChallenge challenge, PDimension challengeSize ) {

        // title
        PNode titleNode = new PhetPText( challenge.title, LineGameConstants.TITLE_FONT, LineGameConstants.TITLE_COLOR );
        addChild( titleNode );
        titleNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ), 10 ); // top center
    }
}
