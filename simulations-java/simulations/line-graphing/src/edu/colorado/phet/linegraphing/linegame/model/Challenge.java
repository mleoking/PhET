// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for game challenges.
 * In all challenges, the user is trying to match a given line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Challenge implements Resettable {

    private static final int GRAPH_WIDTH = 400; // graph width in view coordinates

    public final Line answer; // the correct answer
    public final Property<Line> guess; // the user's current guess

    public final String title;
    public final String description; // dev attribute, for debugging challenge generation, no i18n
    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final Graph graph; // the graph that plots the lines
    public final EquationForm equationForm;
    public final ManipulationMode manipulationMode;
    public final PointTool pointTool1, pointTool2;
    protected boolean answerVisible;

    /**
     * Constructor.
     * @param title title that is visible to the user
     * @param description brief description of the challenge, visible in dev versions
     * @param answer the correct answer
     * @param equationForm see EquationForm
     * @param manipulationMode see ManipulationMode
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @param originOffset offset of the origin (center of the graph) in view coordinates
     * @param pointToolLocation1 location of point tool in model coordinates
     * @param pointToolLocation2 location of point tool in model coordinates
     */
    protected Challenge( String title, String description,
                         Line answer,
                         EquationForm equationForm, ManipulationMode manipulationMode,
                         IntegerRange xRange, IntegerRange yRange,
                         Point2D originOffset,
                         Vector2D pointToolLocation1, Vector2D pointToolLocation2 ) {

        assert( !answer.undefinedSlope() ); // our answer should be defined

        this.title = title;
        this.description = description;
        this.answer = answer.withColor( LineGameConstants.ANSWER_COLOR );
        this.guess = new Property<Line>( createInitialGuess( answer, manipulationMode ) );

        this.equationForm = equationForm;
        this.manipulationMode = manipulationMode;

        // model-view transform, created in the model because each challenge subclass may have its own transform
        final double mvtScale = GRAPH_WIDTH / xRange.getLength(); // view units / model units
        mvt = ModelViewTransform.createOffsetScaleMapping( originOffset, mvtScale, -mvtScale ); // graph on right, y inverted

        // Graph
        graph = new Graph( xRange, yRange );

        // Point tools
        this.pointTool1 = new PointTool( pointToolLocation1, Orientation.UP, graph.lines );
        this.pointTool2 = new PointTool( pointToolLocation2, Orientation.DOWN, graph.lines );

        // When the guess changes, update the lines that are "seen" by the point tools.
        this.guess.addObserver( new SimpleObserver() {
            public void update() {
                updateGraphLines();
            }
        } );
    }

    // Resets the challenge
    public void reset() {
        guess.reset();
        pointTool1.reset();
        pointTool2.reset();
        setAnswerVisible( false );
    }

    /**
     * Creates the view component for the challenge.
     * @param model the game model
     * @param challengeSize dimensions of the view rectangle that is available for rendering the challenge
     * @param audioPlayer the audio player, for providing audio feedback during game play
     */
    public abstract ChallengeNode createView( LineGameModel model, PDimension challengeSize, GameAudioPlayer audioPlayer );

    // True if the guess and answer are descriptions of the same line.
    public boolean isCorrect() {
        return answer.same( guess.get() );
    }

    // Visibility of the answer affects what is "seen" by the point tools.
    public void setAnswerVisible( boolean visible ) {
        answerVisible = visible;
        updateGraphLines();
    }

    // Updates the graph lines, which is the collection of lines that are "seen" by the point tools.
    protected abstract void updateGraphLines();

    // Creates an initial guess, based on the answer and what the user can manipulate..
    private static Line createInitialGuess( Line answer, ManipulationMode manipulationMode ) {
        if ( manipulationMode == ManipulationMode.SLOPE ) {
            // slope is variable, so use the answer's point
            return Line.createPointSlope( answer.x1, answer.y1, 1, 1, LineGameConstants.GUESS_COLOR );
        }
        else if ( manipulationMode == ManipulationMode.INTERCEPT ) {
            // intercept is variable, so use the answer's slope
            return Line.createSlopeIntercept( answer.rise, answer.run, 0, LineGameConstants.GUESS_COLOR );
        }
        else if ( manipulationMode == ManipulationMode.POINT ) {
            // point is variable, so use the answer's slope
            return Line.createPointSlope( 0, 0, answer.rise, answer.run, LineGameConstants.GUESS_COLOR );
        }
        else if ( manipulationMode == ManipulationMode.THREE_POINTS ) {
            return null; // 3 points don't initially form a line
        }
        else {
            // in all other cases, use the standard line y=x
            return Line.Y_EQUALS_X_LINE.withColor( LineGameConstants.GUESS_COLOR );
        }
    }

    // Creates a standard title for the challenge, based on what the user can manipulate.
    protected static String createTitle( String defaultTitle, ManipulationMode manipulationMode ) {
        if ( manipulationMode == ManipulationMode.SLOPE ) {
            return Strings.SET_THE_SLOPE;
        }
        else if ( manipulationMode == ManipulationMode.INTERCEPT ) {
            return Strings.SET_THE_Y_INTERCEPT;
        }
        else if ( manipulationMode == ManipulationMode.POINT ) {
            return Strings.SET_THE_POINT;
        }
        else if ( manipulationMode == ManipulationMode.THREE_POINTS ) {
            return Strings.PLACE_THE_POINTS;
        }
        else {
            return defaultTitle;
        }
    }
}
