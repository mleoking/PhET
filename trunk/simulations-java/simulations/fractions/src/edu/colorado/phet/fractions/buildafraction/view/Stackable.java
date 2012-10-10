// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.Option;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for something that is stackable (such as number cards or shape pieces).
 *
 * @author Sam Reid
 */
public abstract class Stackable<T extends Stackable> extends PNode {
    private Option<Integer> positionInStack = Option.none();
    protected Stack<T> stack;
    public final BooleanProperty animating = new BooleanProperty( false );

    public void setAllPickable( boolean pickable ) {

        //Don't make something animating pickable, see DisablePickingWhileAnimating
        if ( !pickable || !animating.get() ) {
            setPickable( pickable );
            setChildrenPickable( pickable );
        }
    }

    public Option<Integer> getPositionInStack() { return positionInStack; }

    public void setPositionInStack( Option<Integer> positionInStack ) { this.positionInStack = positionInStack; }

    public abstract void animateToStackLocation( Vector2D v, boolean deleteOnArrival );

    protected abstract double getAnimateToScale();

    public boolean isInStack() { return positionInStack.isSome(); }

    public boolean isAtStackIndex( Integer site ) { return positionInStack.isSome() && positionInStack.some().equals( site ); }

    public void setStack( final Stack<T> stack ) {
        assert this.stack == null;
        addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange( final PropertyChangeEvent evt ) {
                stack.cardMoved();
            }
        } );
        this.stack = stack;
    }

    protected void delete() {
        stack.cards = stack.cards.filter( new F<T, Boolean>() {
            @Override public Boolean f( final T t ) {
                return t != Stackable.this;
            }
        } );
        stack.update();
    }
}