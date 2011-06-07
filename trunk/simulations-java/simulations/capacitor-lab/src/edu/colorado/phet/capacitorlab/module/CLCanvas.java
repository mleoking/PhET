// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for all canvases in the "Capacitor Lab" sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class CLCanvas extends PhetPCanvas {

    private final CLModel model;
    private final CLModelViewTransform3D mvt; // model-view transform
    private final PNode rootNode; // root node of our scenegraph, all nodes added below here

    public CLCanvas( CLModel model, CLModelViewTransform3D mvt ) {
        super( CLConstants.CANVAS_RENDERING_SIZE );
        setBackground( CLPaints.CANVAS_BACKGROUND );

        this.model = model;
        this.mvt = mvt;

        rootNode = new PNode();
        addWorldChild( rootNode );
    }

    public void reset() {
        // nothing to do
    }

    // Adds a child node to the root node.
    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    // Removes a child node from the root node.
    protected void removeChild( PNode node ) {
        if ( node != null && rootNode.indexOfChild( node ) != -1 ) {
            rootNode.removeChild( node );
        }
    }

    @Override protected void updateLayout() {
        super.updateLayout();

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }

        // adjust the world bounds
        Point3D p = mvt.viewToModelDelta( worldSize.getWidth(), worldSize.getHeight() );
        model.getWorldBounds().setBounds( 0, 0, p.getX(), p.getY() );
    }
}
