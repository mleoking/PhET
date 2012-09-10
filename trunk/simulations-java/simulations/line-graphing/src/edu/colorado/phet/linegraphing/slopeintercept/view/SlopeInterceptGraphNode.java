// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
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
                                    Property<Line> interactiveLine,
                                    ObservableList<Line> savedLines,
                                    ObservableList<Line> standardLines,
                                    Property<Boolean> linesVisible,
                                    Property<Boolean> interactiveLineVisible,
                                    Property<Boolean> interactiveEquationVisible,
                                    Property<Boolean> slopeVisible,
                                    Property<DoubleRange> x1Range,
                                    Property<DoubleRange> y1Range,
                                    Property<DoubleRange> riseRange,
                                    Property<DoubleRange> runRange ) {
        super( graph, mvt,
               interactiveLine, savedLines, standardLines,
               linesVisible, interactiveLineVisible, interactiveEquationVisible, slopeVisible,
               x1Range, y1Range, riseRange, runRange,
               LGColors.INTERCEPT, LGColors.SLOPE );
    }

    // Creates a node that displays the line in slope-intercept form.
    protected StraightLineNode createLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new SlopeInterceptLineNode( line, graph, mvt );
    }
}
