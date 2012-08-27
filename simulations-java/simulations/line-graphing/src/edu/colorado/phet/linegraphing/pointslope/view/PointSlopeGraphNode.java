// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

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
 * Graph that provides direct manipulation of a line in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeGraphNode extends LineFormsGraphNode {

    public PointSlopeGraphNode( final Graph graph, final ModelViewTransform mvt,
                                Property<PointSlopeLine> interactiveLine,
                                ObservableList<PointSlopeLine> savedLines,
                                ObservableList<PointSlopeLine> standardLines,
                                Property<Boolean> linesVisible,
                                Property<Boolean> interactiveLineVisible,
                                Property<Boolean> interactiveEquationVisible,
                                Property<Boolean> slopeVisible,
                                Property<DoubleRange> riseRange,
                                Property<DoubleRange> runRange,
                                Property<DoubleRange> x1Range,
                                Property<DoubleRange> y1Range ) {
        super( graph, mvt,
               interactiveLine, savedLines, standardLines,
               linesVisible, interactiveLineVisible, interactiveEquationVisible, slopeVisible,
               riseRange, runRange, x1Range, y1Range,
               LGColors.POINT_X1_Y1, LGColors.SLOPE );
    }

    // Creates a node that displays the line in point-slope form.
    protected StraightLineNode createLineNode( PointSlopeLine line, Graph graph, ModelViewTransform mvt ) {
       return new PointSlopeLineNode( line, graph, mvt );
    }
}
