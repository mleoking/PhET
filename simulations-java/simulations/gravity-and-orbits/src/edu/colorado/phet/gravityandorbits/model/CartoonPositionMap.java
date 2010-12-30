package edu.colorado.phet.gravityandorbits.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Converts between cartoon and real coordinates for Body instances.
 *
 * @author Sam Reid
 */
public class CartoonPositionMap {
    private double cartoonOffsetScale;

    public CartoonPositionMap( double cartoonOffsetScale ) {
        this.cartoonOffsetScale = cartoonOffsetScale;
    }

    public ImmutableVector2D toCartoon( ImmutableVector2D position, ImmutableVector2D parentPosition ) {
        return parentPosition.plus( position.minus( parentPosition ).times( cartoonOffsetScale ) );
    }

    //solve for x given cartoonx:
    //cartoonx = parent.x+(x - parent.x) * scale
    //cartoonx = parent.x+x*scale - parent.x*scale
    //(cartoonx -parent.x+parent.x*scale)/scale = x
    //TODO: convert this program to scala
    public ImmutableVector2D toReal( ImmutableVector2D cartoonPosition, ImmutableVector2D parentPosition ) {
        return cartoonPosition.minus( parentPosition ).plus( parentPosition.times( cartoonOffsetScale ) ).times( 1.0 / cartoonOffsetScale );
    }
}
