package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

/**
 * @author Sam Reid
 */
public @Data class GameOverScore {
    public final int level;
    public final int score;
    public final int maxPoints;
}