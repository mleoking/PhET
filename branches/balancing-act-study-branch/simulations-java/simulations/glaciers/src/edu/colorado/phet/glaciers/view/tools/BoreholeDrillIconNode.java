// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.GlaciersModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolIconNode.InteractiveToolIconNode;

/**
 * BoreholeDrillIconNode
 */
public class BoreholeDrillIconNode extends InteractiveToolIconNode {
    
    // NOTE! image specific, offset of bit tip from handle in local view coordinates
    private static final Point2D DRAG_OFFSET = new Point2D.Double( -30, 90 );
    
    public BoreholeDrillIconNode( IToolProducer toolProducer, GlaciersModelViewTransform mvt  ) {
        super( BoreholeDrillNode.createImage(), toolProducer, mvt );
    }
    
    public AbstractTool createTool( Point2D position ) {
        return getToolProducer().addBoreholeDrill( position );
    }
    
    protected Point2D getDragOffsetReference() {
        return DRAG_OFFSET;
    }
}