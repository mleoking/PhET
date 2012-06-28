// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.view;

import fj.F;
import lombok.Data;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

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