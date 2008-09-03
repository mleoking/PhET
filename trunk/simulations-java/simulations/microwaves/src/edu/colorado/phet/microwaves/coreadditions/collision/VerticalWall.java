/*
 * Class: HorizontalWall
 * Package: edu.colorado.phet.physics
 *
 * Created by: Ron LeMaster
 * Date: Dec 12, 2002
 */
package edu.colorado.phet.microwaves.coreadditions.collision;

import java.awt.geom.Point2D;

/**
 * A vertically oriented wall. It's y position is undefined. When asked its y position, it reports Double.NaN
 */
public class VerticalWall extends Wall {

    /**
     *
     */
    public VerticalWall( float x1, float x2, float y1, float y2, int direction ) {

        super();
        this.setLocation( x1, x2, y1, y2 );
    }

    /**
     *
     */
    public void setLocation( float x1, float x2, float y1, float y2 ) {
        if( x1 != x2 ) {
            throw new RuntimeException( "Non-vertical coordinates specified for vertical wall" );
        }
        super.setLocation( new Point2D.Float( x1, y1 ), new Point2D.Float( x2, y2 ) );
//        this.setPosition( x1, Float.NaN );
    }

    //
    // Static fields and methods
    //
    public static int FACING_LEFT = 1;
    public static int FACING_RIGHT = 2;
}
