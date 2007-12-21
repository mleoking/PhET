/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.io.IOException;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.SVGNode;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.umd.cs.piccolo.PNode;


public class PenguinNode extends SVGNode {

    public PenguinNode( PhetPCanvas canvas, PNode dragBoundsNode ) throws IOException {
        super( canvas, GlaciersResources.getResourceAsStream( "images/penguin.svg" ), 50, 50 );
        BoundedDragHandler dragHandler = new BoundedDragHandler( this, dragBoundsNode );
        addInputEventListener( dragHandler );
        addInputEventListener( new CursorHandler() );
    }
}
