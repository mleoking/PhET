// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;

/**
 * Used in ToolboxNode to create nodes when a ToolIconNode is dragged out of the toolbox.
 *
 * @author Sam Reid
 */
public interface NodeFactory {
    //Creates a ToolNode that will be dragged out of the toolbox
    ToolNode createNode( ModelViewTransform transform, Property<Boolean> visible, Point2D location );
}
