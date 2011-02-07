// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Properties related to game settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSettings {

    private final ConstrainedIntegerProperty levelProperty;
    private final Property<Boolean> soundEnabledProperty;
    private final Property<Boolean> timerEnabledProperty;

    public GameSettings( IntegerRange levelsRange, boolean soundEnabled, boolean timerEnabled ) {
        this.levelProperty = new ConstrainedIntegerProperty( levelsRange );
        this.soundEnabledProperty = new Property<Boolean>( soundEnabled );
        this.timerEnabledProperty = new Property<Boolean>( timerEnabled );
    }

    public void reset() {
        levelProperty.reset();
        soundEnabledProperty.reset();
        timerEnabledProperty.reset();
    }

    public void setLevel( int level ) {
        levelProperty.setValue( level );
    }

    public int getLevel() {
        return levelProperty.getValue();
    }

    public ConstrainedIntegerProperty getLevelProperty() {
        return levelProperty;
    }

    public void setSoundEnabled( boolean enabled ) {
        soundEnabledProperty.setValue( enabled );
    }

    public boolean isSoundEnabled() {
        return soundEnabledProperty.getValue();
    }

    public Property<Boolean> getSoundEnabledProperty() {
        return soundEnabledProperty;
    }

    public void setTimerEnabled( boolean enabled ) {
        timerEnabledProperty.setValue( enabled );
    }

    public boolean isTimerEnabled() {
        return timerEnabledProperty.getValue();
    }

    public Property<Boolean> getTimerEnabledProperty() {
        return timerEnabledProperty;
    }
}
