package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.piccolo.PhetPNode;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:37 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CircuitNode extends PhetPNode {
    public void addGraphic( Wire wire ) {
        WireNode wireNode = new WireNode( wire );
        addChild( wireNode );
    }
}
