// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * Interface that determines the location of a Stackable piece within the stack.
 *
 * @author Sam Reid
 */
public interface StackContext<T extends Stackable> {
    Vector2D getLocation( int stackIndex, int cardIndex, T card );
}