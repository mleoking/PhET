// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import lombok.Data;

import edu.colorado.phet.fractions.buildafraction.view.shapes.IScoreBoxPair;
import edu.umd.cs.piccolo.PNode;

/**
 * A pair of a score box node and the pattern next to it.
 *
 * @author Sam Reid
 */
public @Data class ScoreBoxPair implements IScoreBoxPair {
    public final NumberScoreBoxNode targetCell;
    public final PNode node;
}