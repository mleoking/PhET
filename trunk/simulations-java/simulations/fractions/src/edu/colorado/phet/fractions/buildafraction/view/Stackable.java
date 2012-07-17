// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.data.Option;

import edu.colorado.phet.fractions.common.util.immutable.Vector2D;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public abstract class Stackable extends PNode {
    private Option<Integer> positionInStack = Option.none();

    public void setAllPickable( boolean pickable ) {
        setPickable( pickable );
        setChildrenPickable( pickable );
    }

    public Option<Integer> getPositionInStack() {
        return positionInStack;
    }

    public void setPositionInStack( Option<Integer> positionInStack ) {
        this.positionInStack = positionInStack;
    }

    public void animateTo( Vector2D v ) {
        animateToPositionScaleRotation( v.x, v.y, 1, 0, 1000 );
    }

    public boolean isAtStackIndex( Integer site ) {
        return positionInStack.isSome() && positionInStack.some().equals( site );
    }
}