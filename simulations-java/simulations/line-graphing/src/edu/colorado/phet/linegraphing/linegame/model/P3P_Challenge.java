// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.IChallenge;
import edu.colorado.phet.linegraphing.linegame.model.LineForm;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.placepoints.P3P_ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Challenges where the user must "Place 3 Points" (P3P) on a given line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class P3P_Challenge implements IChallenge {

    private static final int GRAPH_WIDTH = 400; // graph width in view coordinates
    private static final Point2D ORIGIN_OFFSET = new Point2D.Double( 700, 300 ); // offset of the origin (center of the graph) in view coordinates

    public final String title;
    public final Line answer;
    public final LineForm lineForm;
    public Property<Line> guess;
    public final Property<Vector2D> p1, p2, p3; // points that the user places

    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final Graph graph; // the graph that plots the lines
    public final PointTool pointTool1, pointTool2;
    private final ObservableList<Line> allLines;
    private boolean answerVisible;

    public P3P_Challenge( Line answer, LineForm lineForm ) {
        this( answer, lineForm, LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
    }

    private P3P_Challenge( Line answer, LineForm lineForm, IntegerRange xRange, IntegerRange yRange ) {

        this.title = Strings.PLACE_THE_POINTS;
        this.answer = answer.withColor( LineGameConstants.ANSWER_COLOR );
        this.lineForm = lineForm;
        this.guess = new Property<Line>( null );
        this.p1 = new Property<Vector2D>( new Vector2D( -3, 2 ) );
        this.p2 = new Property<Vector2D>( new Vector2D( 0, 0 ) );
        this.p3 = new Property<Vector2D>( new Vector2D( 3, 2 ) );

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
        return answer.onLine( p1.get() ) && answer.onLine( p2.get() ) && answer.onLine( p3.get() );
    }

    public void setAnswerVisible( boolean visible ) {
        answerVisible = visible;
        updateLines();
    }

    // Updates the list of lines that are visible to the point tools.
    private void updateLines() {

        // the user's guess, if the 3 points make a straight line
        Line guess = new Line( p1.get().x, p1.get().y, p2.get().x, p2.get().y, LineGameConstants.GUESS_COLOR );
        if ( guess.onLine( p3.get() ) ) {
            this.guess.set( guess );
        }
        else {
            this.guess.set( null );
        }

        // the lines to display
        allLines.clear();
        if ( this.guess.get() != null ) {
            allLines.add( this.guess.get() );
        }
        if ( answerVisible ) {
            allLines.add( answer );
        }
    }

    // Creates the view for this challenge.
    public ChallengeNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new P3P_ChallengeNode( model, this, audioPlayer, challengeSize );
    }
}
