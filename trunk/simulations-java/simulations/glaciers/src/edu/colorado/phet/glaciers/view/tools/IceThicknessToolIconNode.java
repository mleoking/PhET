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
 * IceThicknessToolIconNode
 */
public class IceThicknessToolIconNode extends InteractiveToolIconNode {
    
    public IceThicknessToolIconNode( IToolProducer toolProducer, ModelViewTransform mvt  ) {
        super( GlaciersImages.ICE_THICKNESS_TOOL, GlaciersStrings.TOOLBOX_ICE_THICKNESS_TOOL, toolProducer, mvt );
    }
    
    public AbstractTool createTool( Point2D position ) {
        return getToolProducer().addIceThicknessTool( position );
    }
}