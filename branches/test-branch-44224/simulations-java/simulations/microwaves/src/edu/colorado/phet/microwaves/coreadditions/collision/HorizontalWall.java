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
 * A horizontally oriented wall. It's x position is undefined. When asked its x position, it reports Double.NaN
 */
public class HorizontalWall extends Wall {

    private float y;

    public HorizontalWall( float x1, float x2, float y1, float y2, int direction ) {
        super();
        this.setLocation( x1, x2, y1, y2 );
    }

    public void setLocation( float x1, float x2, float y1, float y2 ) {
        if ( y1 != y2 ) {
            throw new RuntimeException( "Non-horizontal coordinates specified for horizontal wall" );
        }
        super.setLocation( new Point2D.Float( x1, y1 ), new Point2D.Float( x2, y2 ) );
        this.y = y1;
//        this.setPosition( Float.NaN, y1 );
    }

//    public Vector2D getPosition() {
//        return super.getPosition();
//    }

    //
    // Static fields and methods
    //
    public static int FACING_UP = 1;
    public static int FACING_DOWN = 2;
}
