// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.graphtheline;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;

/**
 * Base class model for all "Graph the Line" (GTL) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GTL_Challenge extends Challenge {

    private static final int GRAPH_WIDTH = 400; // graph width in view coordinates
    private static final Point2D ORIGIN_OFFSET = new Point2D.Double( 700, 300 ); // offset of the origin (center of the graph) in view coordinates

    public final Graph graph; // the graph that plots the lines
    public final ModelViewTransform mvt; // transform between model and view coordinate frames

    public GTL_Challenge( Line answer, Line guess ) {
        this( answer, guess, LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
    }

    private GTL_Challenge( Line answer, Line guess, IntegerRange xRange, IntegerRange yRange ) {
        super( answer, guess );

        graph = new Graph( xRange, yRange );

        final double mvtScale = GRAPH_WIDTH / xRange.getLength(); // view units / model units
        mvt = ModelViewTransform.createOffsetScaleMapping( ORIGIN_OFFSET, mvtScale, -mvtScale ); // graph on right, y inverted
    }
}
