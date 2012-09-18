// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import lombok.Data;

/**
 * Site where a shape piece can be dropped within a single container.
 *
 * @author Sam Reid
 */
public @Data class DropLocation {
    public final int container;
}