// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.common.view.LineFormsGraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;

/**
 * Graph that provides direct manipulation of a line in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptGraphNode extends LineFormsGraphNode {

    public SlopeInterceptGraphNode( LineFormsModel model, LineFormsViewProperties viewProperties ) {
        super( model, viewProperties, LGColors.INTERCEPT, LGColors.SLOPE );
    }

    // Creates a node that displays the line in slope-intercept form.
    protected LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeInterceptLineNode( line, graph, mvt );
    }
}
