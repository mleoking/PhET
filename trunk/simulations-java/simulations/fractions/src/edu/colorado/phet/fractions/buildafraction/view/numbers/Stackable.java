// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.Option;

import edu.colorado.phet.fractions.common.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public interface Stackable {
    void setAllPickable( boolean pickable );

    void moveToFront();

    Option<Integer> getPositionInStack();

    void setPositionInStack( Option<Integer> index );

    void animateTo( Vector2D location );

    boolean isAtStackIndex( Integer site );
}