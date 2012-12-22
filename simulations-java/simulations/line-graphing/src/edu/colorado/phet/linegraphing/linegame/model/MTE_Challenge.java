// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGConstants;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.colorado.phet.linegraphing.linegame.model.LineForm;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.linegame.model.MatchChallenge;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.maketheequation.MTE_PS_PointSlope_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.maketheequation.MTE_PS_Point_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.maketheequation.MTE_PS_Slope_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.maketheequation.MTE_SI_Intercept_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.maketheequation.MTE_SI_SlopeIntercept_ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.maketheequation.MTE_SI_Slope_ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for all "Make the Equation" (MTE) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MTE_Challenge extends MatchChallenge {

    private static final int GRAPH_WIDTH = 400; // graph width in view coordinates
    private static final Point2D ORIGIN_OFFSET = new Point2D.Double( 275, 300 ); // offset of the origin (center of the graph) in view coordinates

    public final String title;
    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final Graph graph; // the graph that plots the lines
    public final LineForm lineForm;
    public final ManipulationMode manipulationMode;
    public final PointTool pointTool1, pointTool2;
    private final ObservableList<Line> allLines;
    private boolean answerVisible;

    // Challenge with default title.
    public MTE_Challenge( Line answer, LineForm lineForm, ManipulationMode manipulationMode ) {
        this( answer, lineForm, manipulationMode, LGConstants.X_AXIS_RANGE, LGConstants.Y_AXIS_RANGE );
    }

    private MTE_Challenge( Line answer, LineForm lineForm, ManipulationMode manipulationMode, IntegerRange xRange, IntegerRange yRange ) {
        super( answer, Line.Y_EQUALS_X_LINE );

        this.lineForm = lineForm;
        this.manipulationMode = manipulationMode;

        // Adjust the title and initial state of the guess.
        if ( lineForm == LineForm.SLOPE_INTERCEPT ) {
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                title = Strings.SET_THE_SLOPE;
                guess.set( Line.createSlopeIntercept( 1, 1, answer.y1 ) );
            }
            else if ( manipulationMode == ManipulationMode.INTERCEPT ) {
                title = Strings.SET_THE_Y_INTERCEPT;
                guess.set( Line.createSlopeIntercept( answer.rise, answer.run, 0 ) );
            }
            else {
                title = Strings.MAKE_THE_EQUATION;
                // default guess is fine
            }
        }
        else {
            if ( manipulationMode == ManipulationMode.POINT ) {
                title = Strings.SET_THE_POINT;
                guess.set( Line.createPointSlope( 0, 0, answer.rise, answer.run ) );
            }
            else if ( manipulationMode == ManipulationMode.SLOPE ) {
                title = Strings.SET_THE_SLOPE;
                guess.set( Line.createPointSlope( answer.x1, answer.y1, 1, 1 ) );
            }
            else {
                title = Strings.MAKE_THE_EQUATION;
                // default guess is fine
            }
        }

        final double mvtScale = GRAPH_WIDTH / xRange.getLength(); // view units / model units
        mvt = ModelViewTransform.createOffsetScaleMapping( ORIGIN_OFFSET, mvtScale, -mvtScale ); // graph on right, y inverted

        graph = new Graph( xRange, yRange );

        allLines = new ObservableList<Line>();
        allLines.add( answer );
        this.pointTool1 = new PointTool( new Vector2D( xRange.getMin() + ( 0.05 * xRange.getLength() ), yRange.getMin() - 1.5 ), Orientation.UP, allLines );
        this.pointTool2 = new PointTool( new Vector2D( xRange.getMin() + ( 0.35 * xRange.getLength() ), yRange.getMin() - 4.5 ), Orientation.DOWN, allLines );

        this.guess.addObserver( new SimpleObserver() {
            public void update() {
                updateLines();
            }
        } );
    }

    // Visibility of the answer affects whether the user's guess is "seen" by the point tools.
    public void setAnswerVisible( boolean visible ) {
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
    }

    // Creates the view that corresponds to the lineForm and manipulationMode.
    public ChallengeNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        if ( lineForm == LineForm.SLOPE_INTERCEPT ) {
            if ( manipulationMode == ManipulationMode.SLOPE ) {
                return new MTE_SI_Slope_ChallengeNode( model, this, audioPlayer, challengeSize );
            }
            else if ( manipulationMode == ManipulationMode.INTERCEPT ) {
                return new MTE_SI_Intercept_ChallengeNode( model, this, audioPlayer, challengeSize );
            }
            else if ( manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
                return new MTE_SI_SlopeIntercept_ChallengeNode( model, this, audioPlayer, challengeSize );
            }
        }
        else {
            if ( manipulationMode == ManipulationMode.POINT ) {
                return new MTE_PS_Point_ChallengeNode( model, this, audioPlayer, challengeSize );
            }
            else if ( manipulationMode == ManipulationMode.SLOPE ) {
                return new MTE_PS_Slope_ChallengeNode( model, this, audioPlayer, challengeSize );
            }
            else if ( manipulationMode == ManipulationMode.POINT_SLOPE ) {
                return new MTE_PS_PointSlope_ChallengeNode( model, this, audioPlayer, challengeSize );
            }
        }
        throw new IllegalStateException( "unsupported combination of lineForm and manipulationMode" );
    }
}
