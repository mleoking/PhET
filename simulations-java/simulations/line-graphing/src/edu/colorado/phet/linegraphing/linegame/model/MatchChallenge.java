// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;

/**
 * Base class for game challenges where the user is trying to match some "given" line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MatchChallenge implements IChallenge {

    private static final int GRAPH_WIDTH = 400; // graph width in view coordinates

    public final Line answer; // the correct answer
    public final Property<Line> guess; // the user's current guess

    public final String title;
    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final Graph graph; // the graph that plots the lines
    public final LineForm lineForm;
    public final ManipulationMode manipulationMode;
    public final PointTool pointTool1, pointTool2;
    protected final ObservableList<Line> pointToolLines; // lines that are "seen" by the point tools
    protected boolean answerVisible;

    /**
     * Constructor.
     * @param answer
     * @param lineForm
     * @param manipulationMode
     * @param xRange
     * @param yRange
     * @param originOffset offset of the origin (center of the graph) in view coordinates
     * @param pointToolLocation1 location of point tool in model coordinates
     * @param pointToolLocation2 location of point tool in model coordinates
     */
    protected MatchChallenge( Line answer,
                              LineForm lineForm, ManipulationMode manipulationMode,
                              IntegerRange xRange, IntegerRange yRange,
                              Point2D originOffset, Vector2D pointToolLocation1, Vector2D pointToolLocation2 ) {

        this.answer = answer.withColor( LineGameConstants.ANSWER_COLOR );
        this.guess = new Property<Line>( null ); // this will be initialized below

        this.lineForm = lineForm;
        this.manipulationMode = manipulationMode;

        // Adjust the title and initial state of the guess.
        if ( manipulationMode == ManipulationMode.SLOPE ) {
            title = Strings.SET_THE_SLOPE;
            guess.set( Line.createPointSlope( answer.x1, answer.y1, 1, 1, LineGameConstants.GUESS_COLOR ) );
        }
        else if ( manipulationMode == ManipulationMode.INTERCEPT ) {
            title = Strings.SET_THE_Y_INTERCEPT;
            guess.set( Line.createSlopeIntercept( answer.rise, answer.run, 0, LineGameConstants.GUESS_COLOR ) );
        }
        else if ( manipulationMode == ManipulationMode.POINT ) {
            title = Strings.SET_THE_POINT;
            guess.set( Line.createPointSlope( 0, 0, answer.rise, answer.run, LineGameConstants.GUESS_COLOR ) );
        }
        else {
            title = Strings.GRAPH_THE_LINE;
            guess.set( Line.Y_EQUALS_X_LINE.withColor( LineGameConstants.GUESS_COLOR ) );
        }

        // Create the model-view transform.
        final double mvtScale = GRAPH_WIDTH / xRange.getLength(); // view units / model units
        mvt = ModelViewTransform.createOffsetScaleMapping( originOffset, mvtScale, -mvtScale ); // graph on right, y inverted

        // Graph
        graph = new Graph( xRange, yRange );

        // Point tools
        pointToolLines = new ObservableList<Line>();
        pointToolLines.add( guess.get() );
        this.pointTool1 = new PointTool( pointToolLocation1, Orientation.UP, pointToolLines );
        this.pointTool2 = new PointTool( pointToolLocation2, Orientation.DOWN, pointToolLines );

        // When the guess changes, update the lines that are "seen" by the point tools.
        this.guess.addObserver( new SimpleObserver() {
            public void update() {
                updatePointToolLines();
            }
        } );
    }

    // Correct if the guess and answer are descriptions of the same line.
    public boolean isCorrect() {
        return answer.same( guess.get() );
    }

    // Visibility of the answer affects what is "seen" by the point tools.
    public void setAnswerVisible( boolean visible ) {
        answerVisible = visible;
        updatePointToolLines();
    }

    // Updates the collection of lines that are "seen" by the point tools.
    protected abstract void updatePointToolLines();
}
