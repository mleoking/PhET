// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.maketheequation;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;

/**
 * Base class model for all "Make the Equation" (MTE) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MTE_Challenge extends Challenge {

    private static final int GRAPH_WIDTH = 400; // graph width in view coordinates
    private static final Point2D ORIGIN_OFFSET = new Point2D.Double( 300, 300 ); // offset of the origin (center of the graph) in view coordinates

    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final Graph graph; // the graph that plots the lines
    public final PointTool pointTool1, pointTool2;
    private final ObservableList<Line> allLines;
    private boolean answerVisible;

    public MTE_Challenge( Line answer, Line guess ) {
        this( answer, guess, LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
    }

    private MTE_Challenge( Line answer, Line guess, IntegerRange xRange, IntegerRange yRange ) {
        super( answer, guess );

        final double mvtScale = GRAPH_WIDTH / xRange.getLength(); // view units / model units
        mvt = ModelViewTransform.createOffsetScaleMapping( ORIGIN_OFFSET, mvtScale, -mvtScale ); // graph on right, y inverted

        graph = new Graph( xRange, yRange );

        allLines = new ObservableList<Line>();
        allLines.add( answer );
        this.pointTool1 = new PointTool( new Vector2D( xRange.getMin() + ( 0.05 * xRange.getLength() ), yRange.getMin() - 1 ), Orientation.UP, allLines );
        this.pointTool2 = new PointTool( new Vector2D( xRange.getMin() + ( 0.35 * xRange.getLength() ), yRange.getMin() - 4 ), Orientation.DOWN, allLines );

        this.guess.addObserver( new SimpleObserver() {
            public void update() {
                updateLines();
            }
        } );
    }

    // Visibility of the answer affects whether the user's guess is "seen" by the point tools.
    @Override public void setAnswerVisible( boolean visible ) {
        answerVisible = visible;
        updateLines();
    }

    // Updates the list of lines that are visible to the point tools.
    private void updateLines() {
        allLines.clear();
        allLines.add( answer );
        if ( answerVisible ) {
            allLines.add( guess.get() );
        }
        else {
            allLines.remove( guess.get() );
        }
    }
}
