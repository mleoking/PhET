// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.control;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.model.LayoutBounds;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.VIEW_PADDING;

public class TimePanel extends PNode {
    public TimePanel( IClock clock, LayoutBounds layoutBounds ) {
        final PSwing timeNode = new PSwing( new PiccoloClockControlPanel( clock ) );
        addChild( timeNode );

        setOffset( VIEW_PADDING, VIEW_PADDING * 2 + ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM.modelToViewDeltaX( layoutBounds.getAvailableKitBounds().getHeight() ) );
    }
}
