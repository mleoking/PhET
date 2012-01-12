// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas (play area) for this application, populates the scenegraph with rectangles from the model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RectanglesCanvas extends PhetPCanvas {

    public RectanglesCanvas( IRectanglesModel model ) {
        super( new Dimension( 1024, 768 ) );

        // all nodes will be children of this root node
        PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // add a node for each rectangle in the model
        for ( Rectangle rectangle : model.getRectangles() ) {
            rootNode.addChild( new RectangleNode( rectangle ) );
        }
    }
}
