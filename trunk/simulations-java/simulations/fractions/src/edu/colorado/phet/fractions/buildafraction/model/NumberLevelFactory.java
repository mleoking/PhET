// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;

/**
 * @author Sam Reid
 */
public interface NumberLevelFactory {
    public NumberLevel createLevel( int level );
}