// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptLineNode;

/**
 * Graph for all "Graph the Line" (GTL) challenges that use slope-intercept (SI) form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GTL_SI_GraphNode extends GTL_GraphNode {

    public GTL_SI_GraphNode( Graph graph, Line answerLine, ModelViewTransform mvt ) {
        super( graph, answerLine, mvt );
    }

    @Override protected LineNode createAnswerLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeInterceptLineNode( line, graph, mvt );
    }

    @Override protected LineNode createGuessLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeInterceptLineNode( line, graph, mvt );
    }
}
