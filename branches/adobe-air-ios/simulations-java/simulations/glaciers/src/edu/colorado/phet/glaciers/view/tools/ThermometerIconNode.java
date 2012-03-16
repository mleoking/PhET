// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.GlaciersModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolIconNode.InteractiveToolIconNode;

/**
 * ThermometerIconNode
 */
public class ThermometerIconNode extends InteractiveToolIconNode {
    
    public ThermometerIconNode( IToolProducer toolProducer, GlaciersModelViewTransform mvt ) {
        super( ThermometerNode.createImage(), toolProducer, mvt );
    }
    
    public AbstractTool createTool( Point2D position ) {
        return getToolProducer().addThermometer( position );
    }
}