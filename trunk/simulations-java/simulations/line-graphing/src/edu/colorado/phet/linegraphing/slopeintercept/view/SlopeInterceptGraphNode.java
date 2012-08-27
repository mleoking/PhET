// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;
import edu.colorado.phet.linegraphing.common.view.LineFormsGraphNode;
import edu.colorado.phet.linegraphing.common.view.StraightLineNode;

/**
 * Graph that provides direct manipulation of a line in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptGraphNode extends LineFormsGraphNode {

    public SlopeInterceptGraphNode( final Graph graph,
                                    final ModelViewTransform mvt,
                                    Property<PointSlopeLine> interactiveLine,
                                    ObservableList<PointSlopeLine> savedLines,
                                    ObservableList<PointSlopeLine> standardLines,
                                    Property<Boolean> linesVisible,
                                    Property<Boolean> interactiveLineVisible,
                                    Property<Boolean> interactiveEquationVisible,
                                    Property<Boolean> slopeVisible,
                                    Property<DoubleRange> riseRange,
                                    Property<DoubleRange> runRange,
                                    Property<DoubleRange> yInterceptRange ) {
        super( graph, mvt,
               interactiveLine, savedLines, standardLines,
               linesVisible, interactiveLineVisible, interactiveEquationVisible, slopeVisible,
               riseRange, runRange, new Property<DoubleRange>( new DoubleRange( 0, 0 ) ) /* x1 is fixed at zero */, yInterceptRange,
               LGColors.INTERCEPT, LGColors.SLOPE );
    }

    // Creates a node that displays the line in slope-intercept form.
    protected StraightLineNode createLineNode( PointSlopeLine line, Graph graph, ModelViewTransform mvt ) {
        assert ( line.x1 == 0 ); // line is in slope intercept form
        return new SlopeInterceptLineNode( line, graph, mvt );
    }
}
