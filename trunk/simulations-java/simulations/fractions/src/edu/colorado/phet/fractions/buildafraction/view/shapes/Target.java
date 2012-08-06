// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import lombok.Data;

import edu.colorado.phet.fractions.common.math.Fraction;
import edu.umd.cs.piccolo.PNode;

/**
 * Data structure for the scoring region.
 *
 * @author Sam Reid
 */
public @Data class Target {
    public final ShapeScoreBoxNode cell;
    public final PNode node;
    public final Fraction value;
}