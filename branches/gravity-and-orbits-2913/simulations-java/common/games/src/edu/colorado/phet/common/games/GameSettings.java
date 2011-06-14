// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.games;

import edu.colorado.phet.common.phetcommon.model.property.ConstrainedIntegerProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Properties related to game settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSettings {

    public final ConstrainedIntegerProperty level;
    public final Property<Boolean> soundEnabled;
    public final Property<Boolean> timerEnabled;

    public GameSettings( IntegerRange levelsRange, boolean soundEnabled, boolean timerEnabled ) {
        this.level = new ConstrainedIntegerProperty( levelsRange );
        this.soundEnabled = new Property<Boolean>( soundEnabled );
        this.timerEnabled = new Property<Boolean>( timerEnabled );
    }

    public int getNumberOfLevels() {
        return level.getMax() - level.getMin() + 1;
    }

    public void reset() {
        level.reset();
        soundEnabled.reset();
        timerEnabled.reset();
    }
}
