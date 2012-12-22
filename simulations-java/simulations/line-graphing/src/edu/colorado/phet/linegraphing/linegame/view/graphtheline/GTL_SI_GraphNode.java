// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.linegame.model.GTL_Challenge;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptLineNode;

/**
 * Graph for all "Graph the Line" (GTL) challenges that use slope-intercept (SI) form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GTL_SI_GraphNode extends GTL_GraphNode {

    public GTL_SI_GraphNode( GTL_Challenge challenge ) {
        super( challenge );
    }

    @Override protected LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeInterceptLineNode( line, graph, mvt );
    }
}
