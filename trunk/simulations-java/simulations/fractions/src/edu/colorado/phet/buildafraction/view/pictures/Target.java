// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view.pictures;

import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
@Data class Target {
    public final PictureScoreBoxNode cell;
    public final PNode node;
    public final Fraction value;
}