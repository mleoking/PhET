// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model.placepoints;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.IChallenge;

/**
 * Base class for challenges where the user must "Place 3 Points" (P3P) on a given line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class P3P_Challenge implements IChallenge {

    private static final int GRAPH_WIDTH = 400; // graph width in view coordinates
    private static final Point2D ORIGIN_OFFSET = new Point2D.Double( 700, 300 ); // offset of the origin (center of the graph) in view coordinates

    public final Line line; // the correct answer
    public final Property<Vector2D> p1, p2, p3; // points that the user places

    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final Graph graph; // the graph that plots the lines
    public final PointTool pointTool1, pointTool2;
    private final ObservableList<Line> allLines;
    private boolean answerVisible;

    public P3P_Challenge( Line line ) {
        this( line, LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
    }

    private P3P_Challenge( Line line, IntegerRange xRange, IntegerRange yRange ) {

        this.line = line;
        this.p1 = new Property<Vector2D>( new Vector2D( -1, -1 ) );
        this.p2 = new Property<Vector2D>( new Vector2D( 0, 0 ) );
        this.p3 = new Property<Vector2D>( new Vector2D( 1, 1 ) );

        final double mvtScale = GRAPH_WIDTH / xRange.getLength(); // view units / model units
        mvt = ModelViewTransform.createOffsetScaleMapping( ORIGIN_OFFSET, mvtScale, -mvtScale ); // graph on right, y inverted

        graph = new Graph( xRange, yRange );

        allLines = new ObservableList<Line>();
        this.pointTool1 = new PointTool( new Vector2D( xRange.getMin() + ( 0.65 * xRange.getLength() ), yRange.getMin() - 1 ), Orientation.UP, allLines );
        this.pointTool2 = new PointTool( new Vector2D( xRange.getMin() + ( 0.95 * xRange.getLength() ), yRange.getMin() - 4 ), Orientation.DOWN, allLines );

        final RichSimpleObserver pointObserver = new RichSimpleObserver() {
            public void update() {
                updateLines();
            }
        };
        pointObserver.observe( p1, p2, p3 );
    }

    // Correct if all points are on the line.
    public boolean isCorrect() {
        return line.onLine( p1.get() ) && line.onLine( p2.get() ) && line.onLine( p3.get() );
    }

    public void setAnswerVisible( boolean visible ) {
        answerVisible = visible;
        updateLines();
    }

    // Updates the list of lines that are visible to the point tools.
    private void updateLines() {

        allLines.clear();

        // the user's guess, if the 3 points make a straight line
        Line guess = new Line( p1.get().x, p1.get().y, p2.get().x, p2.get().y, LineGameConstants.GUESS_COLOR );
        if ( guess.onLine( p3.get() ) ) {
            allLines.add( guess );
        }

        // the answer line
        if ( answerVisible ) {
            allLines.add( line );
        }
    }
}
