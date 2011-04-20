// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Reset all button node for bending light, sets the location to be in the bottom right of the stage
 *
 * @author Sam Reid
 */
public class BendingLightResetAllButtonNode extends ResetAllButtonNode {
    public BendingLightResetAllButtonNode( final Resettable resettable, final Component parent, PDimension stageSize ) {
        super( resettable, parent, 18, Color.black, Color.yellow );
        setConfirmationEnabled( true );//Requested to be enabled on 4/18/2011 with high priority
        //Put the reset all button in the bottom right corner of the stage
        setOffset( stageSize.getWidth() - getFullBounds().getWidth() - 10, stageSize.getHeight() - getFullBounds().getHeight() - 10 );
    }
}
