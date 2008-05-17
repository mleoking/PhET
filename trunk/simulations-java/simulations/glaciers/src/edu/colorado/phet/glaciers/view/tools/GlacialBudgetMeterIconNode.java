/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolIconNode.InteractiveToolIconNode;

/**
 * GlacialBudgetMeterIconNode
 */
public class GlacialBudgetMeterIconNode extends InteractiveToolIconNode {
    
    public GlacialBudgetMeterIconNode( IToolProducer toolProducer, ModelViewTransform mvt  ) {
        super( GlacialBudgetMeterNode.createImage(), toolProducer, mvt );
    }
    
    public AbstractTool createTool( Point2D position ) {
        return getToolProducer().addGlacialBudgetMeter( position );
    }
}