// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.StraightLineNode;

/**
 * Visual representation of a line in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeLineNode extends StraightLineNode {

    public PointSlopeLineNode( final StraightLine line, Graph graph, ModelViewTransform mvt ) {
        super( line, graph, mvt );
    }

    protected EquationNode createEquationNode( StraightLine line, PhetFont font ) {
        return new PointSlopeEquationFactory().createNode( line, font );
    }
}
