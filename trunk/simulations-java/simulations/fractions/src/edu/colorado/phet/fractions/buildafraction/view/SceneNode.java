// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class SceneNode extends PNode {
    public SceneNode( BooleanProperty audioEnabled ) {
        gameAudioPlayer = new GameAudioPlayer( audioEnabled.get() );
        audioEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean enabled ) {
                gameAudioPlayer.setEnabled( enabled );
            }
        } );
    }

    private final GameAudioPlayer gameAudioPlayer;

    public void playSoundForOneComplete() { gameAudioPlayer.correctAnswer(); }

    public void playSoundForAllComplete() { gameAudioPlayer.gameOverPerfectScore(); }
}