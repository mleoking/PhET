/*
 * Class: HorizontalWall
 * Package: edu.colorado.phet.model
 *
 * Created by: Ron LeMaster
 * Date: Dec 12, 2002
 */
package edu.colorado.phet.idealgas.model;

import java.awt.geom.Point2D;

/**
 * A horizontally oriented wall. It's x position is undefined. When asked its x position, it reports Double.NaN
 */
public class HorizontalWall extends Wall {

    //
    // Static fields and methods
    //
    public static int FACING_UP = 1;
    public static int FACING_DOWN = 2;

    private double y;

    public HorizontalWall( double x1, double x2, double y1, double y2, int direction ) {
        super();
        this.setLocation( x1, x2, y1, y2 );

        // Set the position. This is also done in setLocation, but we do it so that the previous position is initialized
        this.setPosition( Double.NaN, y1 );
    }

    public void setLocation( double x1, double x2, double y1, double y2 ) {
        if( y1 != y2 ) {
            throw new RuntimeException( "Non-horizontal coordinates specified for horizontal wall" );
        }
        super.setLocation( new Point2D.Double( x1, y1 ), new Point2D.Double( x2, y2 ) );
        this.y = y1;
        this.setPosition( Double.NaN, y1 );
    }
}
