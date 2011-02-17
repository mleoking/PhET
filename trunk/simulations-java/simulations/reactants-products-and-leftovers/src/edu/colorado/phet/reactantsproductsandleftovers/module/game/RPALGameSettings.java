// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeVisibility;

/**
 * Games settings for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALGameSettings extends GameSettings {

    public final Property<ChallengeVisibility> challengeVisibility; // what parts of the challenge are visible?

    public RPALGameSettings( IntegerRange levelsRange, boolean soundEnabled, boolean timerEnabled, ChallengeVisibility challengeVisibility) {
        super( levelsRange, soundEnabled, timerEnabled );
        this.challengeVisibility = new Property<ChallengeVisibility>( challengeVisibility );
    }
}
