// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class SceneNode extends PNode {
    private final GameAudioPlayer gameAudioPlayer = new GameAudioPlayer( true );

    protected void playSoundForOneComplete() { gameAudioPlayer.correctAnswer(); }

    protected void playSoundForAllComplete() { gameAudioPlayer.gameOverPerfectScore(); }
}