// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class DebugDragLocation extends PBasicInputEventHandler {
    @Override public void mouseDragged( final PInputEvent event ) {
        super.mouseDragged( event );
        final PDimension delta = event.getDeltaRelativeTo( event.getPickedNode().getParent() );
        event.getPickedNode().translate( delta.width / event.getPickedNode().getScale(), delta.height / event.getPickedNode().getScale() );
        System.out.println( event.getPickedNode().getOffset() );
    }
}