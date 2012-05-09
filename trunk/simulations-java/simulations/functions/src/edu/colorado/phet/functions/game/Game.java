// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import fj.F3;
import lombok.Data;

/**
 * @author Sam Reid
 */
public @Data class Game {
    public final GameState initialState;
    public final F3<GameState, Double, UserInput, GameState> update;
}