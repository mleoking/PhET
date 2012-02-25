// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * Cells where the fractions start
 *
 * @author Sam Reid
 */
@Data public class Cell {
    public final ImmutableRectangle2D rectangle;
}