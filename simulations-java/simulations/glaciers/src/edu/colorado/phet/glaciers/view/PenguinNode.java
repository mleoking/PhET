/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


public class PenguinNode extends PImage {

    public PenguinNode( PNode dragBoundsNode ) {
        super( GlaciersResources.getImage( GlaciersConstants.IMAGE_PENGUIN ) );
        BoundedDragHandler dragHandler = new BoundedDragHandler( this, dragBoundsNode );
        addInputEventListener( dragHandler );
        addInputEventListener( new CursorHandler() );
    }
}
