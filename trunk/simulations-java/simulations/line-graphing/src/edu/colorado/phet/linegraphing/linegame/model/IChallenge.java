// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Interface implemented by all game challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IChallenge {

    // Determines if the user's guess is correct.
    public boolean isCorrect();

    // Creates the view component for the challenge.
    public ChallengeNode createView( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize );

    // Do anything that is dependent on visibility of the answer.
    public void setAnswerVisible( boolean visible );

    // Resets the challenge
    public void reset();
}
