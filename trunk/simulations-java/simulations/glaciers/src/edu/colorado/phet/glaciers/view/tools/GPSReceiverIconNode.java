/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolIconNode.InteractiveToolIconNode;

/**
 * GPSReceiverIconNode
 */
public class GPSReceiverIconNode extends InteractiveToolIconNode {
    
    public GPSReceiverIconNode( IToolProducer toolProducer, ModelViewTransform mvt  ) {
        super( GPSReceiverNode.createImage(), GlaciersStrings.TOOLBOX_GPS_RECEIVER, toolProducer, mvt );
    }
    
    public AbstractTool createTool( Point2D position ) {
        return getToolProducer().createGPSReceiver( position );
    }
}