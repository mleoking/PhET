// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import edu.colorado.phet.fractions.common.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public interface StackContext<T extends Stackable> {
    Vector2D getLocation( int stackIndex, int cardIndex, T card );
}