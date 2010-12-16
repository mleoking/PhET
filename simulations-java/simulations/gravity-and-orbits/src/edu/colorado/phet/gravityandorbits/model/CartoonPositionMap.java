package edu.colorado.phet.gravityandorbits.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
public class CartoonPositionMap {
    private double cartoonOffsetScale;

    public CartoonPositionMap( double cartoonOffsetScale ) {
        this.cartoonOffsetScale = cartoonOffsetScale;
    }

    public ImmutableVector2D toCartoon( ImmutableVector2D position, ImmutableVector2D parentPosition ) {
        ImmutableVector2D offset = position.getSubtractedInstance( parentPosition );
        final ImmutableVector2D cartoonOffset = offset.getScaledInstance( cartoonOffsetScale );
        final ImmutableVector2D cartoonPosition = parentPosition.getAddedInstance( cartoonOffset );
        return cartoonPosition;
    }

    //solve for x given cartoonx:
    //cartoonx = parent.x+(x - parent.x) * scale
    //cartoonx = parent.x+x*scale - parent.x*scale
    //(cartoonx -parent.x+parent.x*scale)/scale = x
    //TODO: convert this program to scala
    public ImmutableVector2D toReal( ImmutableVector2D cartoonPosition, ImmutableVector2D parentPosition ) {
        ImmutableVector2D x = ( cartoonPosition.getSubtractedInstance( parentPosition ).getAddedInstance( parentPosition.getScaledInstance( cartoonOffsetScale ) ) );
        x = x.getScaledInstance( 1.0 / cartoonOffsetScale );
        return x;
    }
}
