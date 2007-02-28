/*
 * Class: HorizontalWall
 * Package: edu.colorado.phet.model
 *
 * Created by: Ron LeMaster
 * Date: Dec 12, 2002
 */
package edu.colorado.phet.physics.collision;

import java.awt.geom.Point2D;

/**
 * A vertically oriented wall. It's y position is undefined. When asked its y position, it reports Double.NaN
 */
public class VerticalWall extends Wall {

    /**
     *
     */
    public VerticalWall( double x1, double x2, double y1, double y2, int direction ) {
//    public VerticalWall( double x1, double x2, double y1, double y2, int direction ) {

        super();
        this.setLocation( x1, x2, y1, y2 );

        // Set the position. This is also done in setLocation, but we do it so that the previous position is initialized
        this.setPosition( x1, Double.NaN );
//        this.setPosition( x1, double.NaN );
    }

    /**
     *
     */
    public void setLocation( double x1, double x2, double y1, double y2 ) {
//    public void setLocation( double x1, double x2, double y1, double y2 ) {
        if( x1 != x2 ) {
            throw new RuntimeException( "Non-vertical coordinates specified for vertical wall" );
        }
        super.setLocation( new Point2D.Double( x1, y1 ), new Point2D.Double( x2, y2 ) );
        this.setPosition( x1, Double.NaN );
//        super.setLocation( new Point2D.double( x1, y1 ), new Point2D.double( x2, y2 ) );
//        this.setPosition( x1, double.NaN );
    }

    //
    // Static fields and methods
    //
    public static int FACING_LEFT = 1;
    public static int FACING_RIGHT = 2;
}
