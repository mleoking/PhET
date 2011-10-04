// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class used by all tabs in the sim.
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsCanvas extends PhetPCanvas {

    //Root node that shows the nodes in the stage coordinate frame
    public final PNode rootNode;

    public SugarAndSaltSolutionsCanvas() {

        // Root of the scene graph in stage coordinates (scaled with the window size)
        rootNode = new PNode();
        addWorldChild( rootNode );
    }

    public void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    //Get the root node used for stage coordinates, necessary when transforming through the global coordinate frame to stage
    public PNode getRootNode() {
        return rootNode;
    }
}