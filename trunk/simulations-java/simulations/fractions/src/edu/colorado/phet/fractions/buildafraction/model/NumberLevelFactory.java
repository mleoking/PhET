// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;

/**
 * Factory for creating the different "Number card" levels.
 * This pattern is used so that levels can be created dynamically and resampled when needed.
 *
 * @author Sam Reid
 */
public interface NumberLevelFactory {
    public NumberLevel createLevel( int level );
}