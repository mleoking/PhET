package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;
import lombok.Data;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;

/**
 * Arguments for creating a button.
 *
 * @author Sam Reid
 */
public @Data class ButtonArgs {
    public final IUserComponent component;
    public final String text;
    public final Color color;
    public final Vector2D location;
    public final F<MatchingGameState, MatchingGameState> listener;
}