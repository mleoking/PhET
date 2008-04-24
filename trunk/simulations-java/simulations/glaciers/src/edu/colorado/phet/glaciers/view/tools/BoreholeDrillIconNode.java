/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolIconNode.InteractiveToolIconNode;

/**
 * BoreholeDrillIconNode
 */
public class BoreholeDrillIconNode extends InteractiveToolIconNode {
    
    public BoreholeDrillIconNode( IToolProducer toolProducer, ModelViewTransform mvt  ) {
        super( GlaciersImages.BOREHOLE_DRILL, GlaciersStrings.TOOLBOX_BOREHOLD_DRILL, toolProducer, mvt );
    }
    
    public AbstractTool createTool( Point2D position ) {
        return getToolProducer().addBoreholeDrill( position );
    }
}