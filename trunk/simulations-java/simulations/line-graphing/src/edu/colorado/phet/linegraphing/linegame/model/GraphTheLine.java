// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.GraphTheLineNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for "Graph the Line" challenges.
 * In this challenge, the user is given an equation and must graph the line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphTheLine extends Challenge {

    /**
     * Constructor
     * @param description brief description of the challenge, visible in dev versions
     * @param answer the correct answer
     * @param equationForm see EquationForm
     * @param manipulationMode see ManipulationMode
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     */
    public GraphTheLine( String description, Line answer, EquationForm equationForm, ManipulationMode manipulationMode, IntegerRange xRange, IntegerRange yRange ) {
        super( createTitle( Strings.GRAPH_THE_LINE, manipulationMode ), description,
               answer, equationForm, manipulationMode, xRange, yRange,
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
        if ( guess.get() != null ) {
            pointToolLines.add( guess.get() );
        }
    }

    // Creates the view for this challenge.
    @Override public ChallengeNode createView( LineGameModel model, PDimension challengeSize, GameAudioPlayer audioPlayer ) {
        return new GraphTheLineNode( this, model, challengeSize, audioPlayer );
    }
}
