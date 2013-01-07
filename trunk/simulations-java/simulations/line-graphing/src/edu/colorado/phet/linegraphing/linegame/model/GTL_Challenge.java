// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.GTL_ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for all "Graph the Line" (GTL) challenges.
 * In this challenge, the user is given an equation and must graph the line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_Challenge extends MatchChallenge {

    public GTL_Challenge( String description, Line answer, LineForm lineForm, ManipulationMode manipulationMode, IntegerRange xRange, IntegerRange yRange ) {
        super( createTitle( Strings.GRAPH_THE_LINE, manipulationMode ), description,
               answer, createInitialGuess( answer, manipulationMode ),
               lineForm, manipulationMode,
               xRange, yRange,
               new Point2D.Double( 700, 300 ), // origin offset
               new Vector2D( xRange.getMin() + ( 0.65 * xRange.getLength() ), yRange.getMin() - 1 ), // point tool location 1
               new Vector2D( xRange.getMin() + ( 0.95 * xRange.getLength() ), yRange.getMin() - 4 ) );  // point tool location 2
    }

    // Updates the collection of lines that are "seen" by the point tools.
    @Override protected void updatePointToolLines() {
        pointToolLines.clear();
        if ( answerVisible ) {
            pointToolLines.add( answer );
        }
        pointToolLines.add( guess.get() );
    }

    // Creates the view that corresponds to the lineForm and manipulationMode.
    public ChallengeNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        return new GTL_ChallengeNode( model, this, audioPlayer, challengeSize );
    }
}
