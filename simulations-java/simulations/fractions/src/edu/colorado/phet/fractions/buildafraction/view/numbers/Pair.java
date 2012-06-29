// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import lombok.Data;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public @Data class Pair {
    public final NumberScoreBoxNode targetCell;
    public final PNode patternNode;
}