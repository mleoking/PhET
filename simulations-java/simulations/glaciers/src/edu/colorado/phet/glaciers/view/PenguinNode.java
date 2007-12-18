/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


public class PenguinNode extends PImage {

    public PenguinNode( PNode dragBoundsNode ) {
        super( GlaciersImages.PENGUIN );
        BoundedDragHandler dragHandler = new BoundedDragHandler( this, dragBoundsNode );
        addInputEventListener( dragHandler );
        addInputEventListener( new CursorHandler() );
    }
}
