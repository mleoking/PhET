package edu.colorado.phet.fractionmatcher.model;

import lombok.Data;

/**
 * Data class for the score after a game is finished.
 *
 * @author Sam Reid
 */
public @Data class GameResult {
    public final int level;
    public final int score;
}