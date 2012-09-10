// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;

/**
 * Visual representation of a line in simplified slope-intercept form (y = mx + b).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLineNode extends LineNode {

    public SlopeInterceptLineNode( final Line line, Graph graph, ModelViewTransform mvt ) {
        super( line, graph, mvt );
    }

    // Creates the line's equation in slope-intercept form.
    protected EquationNode createEquationNode( Line line, PhetFont font ) {
        return new SlopeInterceptEquationFactory().createSimplifiedNode( line, font );
    }
}
