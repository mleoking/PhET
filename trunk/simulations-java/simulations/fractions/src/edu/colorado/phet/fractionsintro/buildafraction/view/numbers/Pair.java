package edu.colorado.phet.fractionsintro.buildafraction.view.numbers;

import lombok.Data;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public @Data class Pair {
    public final NumberScoreBoxNode targetCell;
    public final PNode patternNode;
}