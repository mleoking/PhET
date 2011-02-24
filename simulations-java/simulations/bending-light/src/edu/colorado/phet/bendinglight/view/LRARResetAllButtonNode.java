// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;

/**
 * @author Sam Reid
 */
public class LRARResetAllButtonNode extends ResetAllButtonNode {
    public LRARResetAllButtonNode( final Resettable resettable, final Component parent ) {
        super( resettable, parent, 18, Color.black, Color.lightGray );
        setConfirmationEnabled( false );
    }
}
