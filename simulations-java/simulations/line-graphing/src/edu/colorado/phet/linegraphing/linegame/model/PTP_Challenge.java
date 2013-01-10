// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.GTL_ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model for all "Place the Points" (PTP) challenges.
 * This is a specialization of the "Graph the Line" (GTL) challenge.
 * In this challenge, the user is given an equation and must place 3 points on a graph to make the line.
 * If the 3 points do not form a line, the guess line will be null.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PTP_Challenge extends GTL_Challenge {

    public final Property<Vector2D> p1, p2, p3; // the 3 points that the user places

    /**
     * Constructor
     * @param description brief description of the challenge, visible in dev versions
     * @param answer the correct answer
     * @param equationForm see LineForm
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     */
    public PTP_Challenge( String description, final Line answer, EquationForm equationForm, IntegerRange xRange, IntegerRange yRange ) {
        super( description, answer, equationForm, ManipulationMode.THREE_POINTS, xRange, yRange );

        // initial points do not form a line
        this.p1 = new Property<Vector2D>( new Vector2D( -3, 2 ) );
        this.p2 = new Property<Vector2D>( new Vector2D( 0, 0 ) );
        this.p3 = new Property<Vector2D>( new Vector2D( 3, 2 ) );

        // update the guess when the points change
        final RichSimpleObserver pointObserver = new RichSimpleObserver() {
            public void update() {
                Line line = new Line( p1.get().x, p1.get().y, p2.get().x, p2.get().y, LineGameConstants.GUESS_COLOR );
                if ( line.onLine( p3.get() ) ) {
                    // all 3 points are on a line
                    guess.set( line );
                    if ( isCorrect() ) {
                        // when correct, we want the guess to match the answer exactly
                        guess.set( answer );
                    }
                }
                else {
                    // the 3 points don't form a line
                    guess.set( null );
                }
            }
        };
        pointObserver.observe( p1, p2, p3 );
    }

    @Override public void reset() {
        super.reset();
        p1.reset();
        p2.reset();
        p3.reset();
    }

    // Creates the view for this challenge.
    @Override public ChallengeNode createView( LineGameModel model, PDimension challengeSize, GameAudioPlayer audioPlayer ) {
        return new GTL_ChallengeNode( this, model, challengeSize, audioPlayer );
    }
}
