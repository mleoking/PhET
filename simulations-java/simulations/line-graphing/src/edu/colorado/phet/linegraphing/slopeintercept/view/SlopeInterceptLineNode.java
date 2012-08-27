// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.StraightLineNode;

/**
 * Visual representation of a line in simplified slope-intercept form (y = mx + b).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLineNode extends StraightLineNode {

    public SlopeInterceptLineNode( final StraightLine line, Graph graph, ModelViewTransform mvt ) {
        super( line, graph, mvt );
    }

    // Creates equations in slope-intercept form.
    protected EquationNode createEquationNode( StraightLine line, PhetFont font ) {
        assert( line.x1 == 0 ); // line is in slope intercept form
        return new SlopeInterceptEquationFactory().createSimplifiedNode( line, font );
    }
}
