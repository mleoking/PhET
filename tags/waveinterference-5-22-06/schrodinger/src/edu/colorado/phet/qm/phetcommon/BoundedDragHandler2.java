/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * User: Sam Reid
 * Date: Mar 20, 2006
 * Time: 11:52:42 PM
 * Copyright (c) Mar 20, 2006 by Sam Reid
 */

public class BoundedDragHandler2 extends PDragEventHandler {
    public BoundedDragHandler2( PNode bounds ) {
    }

    protected void drag( PInputEvent event ) {
        super.drag( event );
    }
}
