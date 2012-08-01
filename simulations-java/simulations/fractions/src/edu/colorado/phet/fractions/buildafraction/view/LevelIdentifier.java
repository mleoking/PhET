// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import lombok.Data;

/**
 * Uniquely identifies a level, with the index and type.
 *
 * @author Sam Reid
 */
public @Data class LevelIdentifier {
    public final int levelIndex;
    public final LevelType levelType;
}