// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import lombok.Data;

/**
 * Keeps track of how far the user has made it through a level, and what the maximum possible score (in stars) is.
 * Only supports integral number of stars (no fractional stars).
 *
 * @author Sam Reid
 */
public @Data class LevelProgress {
    public final int stars;
    public final int maxStars;
}