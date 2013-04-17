// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.GlaciersModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolIconNode.InteractiveToolIconNode;

/**
 * GPSReceiverIconNode
 */
public class GPSReceiverIconNode extends InteractiveToolIconNode {
    
    public GPSReceiverIconNode( IToolProducer toolProducer, GlaciersModelViewTransform mvt  ) {
        super( GPSReceiverNode.createImage(), toolProducer, mvt );
    }
    
    public AbstractTool createTool( Point2D position ) {
        return getToolProducer().createGPSReceiver( position );
    }
}