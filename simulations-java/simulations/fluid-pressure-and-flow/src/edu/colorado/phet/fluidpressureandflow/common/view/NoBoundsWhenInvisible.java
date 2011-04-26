// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * This class should be used when invisible children shouldn't have any bounds (in Piccolo by default invisible nodes still count towards the bounds union.
 *
 * @author Sam Reid
 */
public class NoBoundsWhenInvisible extends PNode {
    private final PNode child;//The wrapped node that will be displayed

    public NoBoundsWhenInvisible( PNode child ) {
        this.child = child;
        addChild( child );

        //When the child's visibility changes, recompute the bounds since they will probably change
        child.addPropertyChangeListener( PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                invalidateFullBounds();
            }
        } );
    }

    //Determines the size of children bounds (just the content child node), returning an empty bounds if the child is invisible.
    @Override public PBounds getUnionOfChildrenBounds( PBounds dstBounds ) {
        if ( child.getVisible() ) {
            return super.getUnionOfChildrenBounds( dstBounds );
        }
        else {
            if ( dstBounds == null ) {
                return new PBounds();
            }
            else {
                dstBounds.resetToZero();
                return dstBounds;
            }
        }
    }
}
