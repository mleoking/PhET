/*
 * Class: Wall
 * Package: edu.colorado.phet.physics
 *
 * Created by: Ron LeMaster
 * Date: Dec 12, 2002
 */
package edu.colorado.phet.physics.collision;

import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Point2D;

/**
 *
 */
public class Wall extends CollidableBody {

    private Point2D end1;
    private Point2D end2;

    // The line of action unit vector that another body would have if it contacted the wall
    private Vector2D loaUnit;

    protected Wall() {
        super();
        init();
    }

    public Wall( Point2D end1, Point2D end2 ) {
        super();
        init();
        this.setLocation( end1, end2 );

        // set the position twice, so the previous position will also be set
        this.setPosition( new Point2D.Double( end1.getX(), end1.getY() ));
        this.setPosition( new Point2D.Double( end1.getX(), end1.getY() ));
//        this.setPosition( new Vector2D( (float)end1.getX(), (float)end1.getY() ));
//        this.setPosition( new Vector2D( (float)end1.getX(), (float)end1.getY() ));
    }

    public Point2D.Double getCM() {
        throw new RuntimeException( "undefined" );
    }

    public double getMomentOfInertia() {
        throw new RuntimeException( "undefined" );
    }

    private void init() {
        // Set the velocity twice so the previous velocity gets set
        this.setVelocity( 0, 0 );
        this.setVelocity( 0, 0 );
    }

    protected void setLocation( Point2D end1, Point2D end2 ) {
//    protected void setLocation( Point2D.Float end1, Point2D.Float end2 ) {
        this.end1 = end1;
        this.end2 = end2;
        setPosition( end1 );
        setLoaUnit();
//        setChanged();
        notifyObservers();
    }

    public void setLocation( double x1, double x2, double y1, double y2 ) {
        end1.setLocation( x1, y1 );
        end2.setLocation( x2, y2 );
        setPosition( end1 );
//        setChanged();
        notifyObservers();
    }

    private void setLoaUnit() {
        loaUnit = new Vector2D.Double( end2.getY() - end1.getY(),
                                end2.getX() - end1.getX() ).normalize();
//        loaUnit = new Vector2D( (float)( end2.getY() - end1.getY() ),
//                                (float)( end2.getX() - end1.getX() ) ).normalize();
    }

    public Vector2D getLoaUnit( Particle particle ) {
        return loaUnit;
    }

    public Point2D getEnd1() {
        return end1;
    }

    public Point2D getEnd2() {
        return end2;
    }


    //
    // Abstract methods
    //
    public boolean isInContactWithBody( Body body ) {
        return false;
    }


    // Returns the distance from the body's center that it makes contact
    // with other bodies. This is, of course, an over-simplified approach,
    // and only works with walls and spheres.
    public double getContactOffset( Body body ) {
        return 0;
    }

//    public boolean isInContactWithBody( Collidable body ) {
//        return false;
//    }
}

