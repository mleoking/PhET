// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.placepoints;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.linegame.model.placepoints.P3P_Challenge;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptLineNode;

/**
 * Graph for all "Place 3 Point" (P3P) challenges that use slope-intercept (SI) form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class P3P_SI_GraphNode extends P3P_GraphNode {

    public P3P_SI_GraphNode( final P3P_Challenge challenge ) {
        super( challenge );
    }

    @Override protected LineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeInterceptLineNode( line, graph, mvt ) {{
            setEquationVisible( false );
        }};
    }
}
