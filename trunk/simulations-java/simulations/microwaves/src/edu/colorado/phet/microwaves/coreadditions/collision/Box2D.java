/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.microwaves.coreadditions.collision;

//import edu.colorado.phet.physics.PhysicalSystem;
//import edu.colorado.phet.physics.Vector2D;
//import edu.colorado.phet.physics.body.Body;

import edu.colorado.phet.microwaves.coreadditions.CompositeBody;
import edu.colorado.phet.microwaves.coreadditions.Vector2D;

import java.awt.geom.Point2D;


/**
 * A 2 dimensional box
 */
public class Box2D extends CompositeBody {

    private Point2D.Double corner1;
    private Point2D.Double corner2;
    private Point2D.Double center;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private Wall leftWall;
    private Object leftWallMonitor = new Object();
    private double leftWallVx = 0;

    // TODO: put the opening characteristics in a specialization of this class.
    private Vector2D[] opening = new Vector2D[]{
            new Vector2D( 0, 0 ),
            new Vector2D( 0, 0 )};

    private Wall[] walls = new Wall[4];

    public Box2D() {
        super();
    }

    public Box2D( Point2D.Double corner1, Point2D.Double corner2 ) {
        super();
        this.setState( corner1, corner2 );
    }

    public void setBounds( double minX, double minY, double maxX, double maxY ) {
        this.setState( new Point2D.Double( minX, minY ), new Point2D.Double( maxX, maxY ) );
    }

    private void setState( Point2D.Double corner1, Point2D.Double corner2 ) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        maxX = Math.max( corner1.getX(), corner2.getX() );
        maxY = Math.max( corner1.getY(), corner2.getY() );
        minX = Math.max( Math.min( Math.min( corner1.getX(), corner2.getX() ), maxX - 20 ), 0 );
//        minX = Math.max( Math.min( Math.min( corner1.getX(), corner2.getX() ), maxX - 20 ), 40 );
        minY = Math.min( corner1.getY(), corner2.getY() );
        center = new Point2D.Double(
                ( this.maxX + this.minX ) / 2,
                ( this.maxY + this.minY ) / 2 );

        setLocation( minX, minY );

        // Update the position of the door
        Vector2D[] opening = this.getOpening();
        opening[0].setY( (float)minY );
        opening[1].setY( (float)minY );
        this.setOpening( opening );

        // Left wall
        if( walls[0] == null ) {
            walls[0] = new VerticalWall( (float)minX, (float)minX, (float)minY, (float)maxY, VerticalWall.FACING_RIGHT );
        }
        walls[0].setLocation( minX, minX, minY, maxY );
        leftWall = walls[0];

        // Right wall
        if( walls[1] == null ) {
            walls[1] = new VerticalWall( (float)maxX, (float)maxX, (float)minY, (float)maxY, VerticalWall.FACING_LEFT );
        }
        walls[1].setLocation( maxX, maxX, minY, maxY );

        // Top wall
        if( walls[2] == null ) {
            walls[2] = new HorizontalWall( (float)minX, (float)maxX, (float)minY, (float)minY, HorizontalWall.FACING_DOWN );
        }
        walls[2].setLocation( minX, maxX, minY, minY );

        // Bottom wall
        if( walls[3] == null ) {
            walls[3] = new HorizontalWall( (float)minX, (float)maxX, (float)maxY, (float)maxY, HorizontalWall.FACING_UP );
        }
        walls[3].setLocation( minX, maxX, maxY, maxY );

        this.setChanged();
        this.notifyObservers();
    }

    /**
     *
     * @param physicalSystem
     */
//    public void setPhysicalSystem( PhysicalSystem physicalSystem ) {
//        super.setPhysicalSystem( physicalSystem );
//        for( int i = 0; i < walls.length; i++ ) {
//            walls[i].setPhysicalSystem( physicalSystem );
//        }
//    }

    /**
     *
     */
    public void setOpening( Vector2D[] opening ) {
        this.opening[0] = opening[0];
        this.opening[1] = opening[1];
        setChanged();
        notifyObservers();
    }

    /**
     *
     */
    public Vector2D[] getOpening() {
        return this.opening;
    }

    protected Wall getLeftWall() {
        return leftWall;
    }

    /**
     *
     */
    public void stepInTime( float dt ) {
        super.stepInTime( dt );
        synchronized( leftWallMonitor ) {
            leftWall.setVelocity( (float)leftWallVx, leftWall.getVelocity().getY() );
        }
    }

    /**
     * @param particle
     */
/*
    public void collideWithParticle( SphericalBody particle ) {

        // Since we can collide with more than one wall in a time step, and we try to handle that in this method, we
        // also have to make sure that we don't thing we've hit a second wall, when we actually only hit one, but the
        // timing of the collision was such that it happened exactly at the end of the time step. In such a case, the
        // particle will still be in contact with the wall at the end of the time step, and we do not want to treat
        // this as another collision. The following variable is used to handle this.
        Wall previousCollidingWall = null;

        if( isInOpening( particle ) ) {
            return;
        }

        boolean hasCollision = false;
        int cnt = 0;
        do {
            hasCollision = false;
            Wall collidingWall = null;
            double shortestT1 = Double.MAX_VALUE;
            double t1 = 0;

            // See if the particle is hitting any of the walls of the box. If it hits more than one,
            // determine which it hit first
            for( int i = 0; i < walls.length; i++ ) {
                Wall wall = walls[i];
                if( detector.areInContact( particle, wall ) ) {
//                if( ContactDetector.areContacting( particle, wall ) ) {
//                if( wall.isInContactWithParticle( particle ) ) {
                    t1 = CollisionLaw.computeT1( particle, wall );

                    // Is this the earliest wall collision?
                    if( t1 < shortestT1 ) {
                        collidingWall = wall;
                        shortestT1 = t1;
                    }
                }
            }
            if( collidingWall != null && collidingWall != previousCollidingWall ) {
                previousCollidingWall = collidingWall;
                hasCollision = true;
                cnt++;
//                if( cnt > 1 ) {
//                    System.out.println( "Box2D.collideWithParticle(): Multiple collisions in single time step. cnt = " + cnt );
//                }
                CollisionFactory.create( collidingWall, particle ).collide();
//                collidingWall.collideWithParticle( particle );

                // Handle giving particle kinetic energy if the wall is moving
                if( collidingWall == leftWall ) {
                    float vx0 = particle.getVelocity().getX();
                    float vx1 = vx0 + leftWallVx;
                    particle.setVelocityX( vx1 );
                }
            }

        } while( hasCollision && cnt < 2 );
    }
*/

    /**
     *
     */
//    SphereWallContactDetector detector = new SphereWallContactDetector();
/*
    public boolean isInContactWithParticle( SphericalBody particle ) {

        if( isInOpening( particle ) ) {
            return false;
        }
        // To try to catch escaped particles
        if( this.containsBody( particle ) && this.isOutsideBox( particle ) ) {
            return true;
        }

        for( int i = 0; i < walls.length; i++ ) {
            Wall wall = walls[i];
            if( detector.areInContact( wall, particle ) ) {
                return true;
            }
        }
        return false;
    }
*/

    /**
     *
     */
    private Vector2D closestCornerResult = new Vector2D();

    public Vector2D getClosestCorner( Vector2D point ) {

        double x = Math.abs( point.getX() - minX ) < Math.abs( point.getX() - maxX )
                   ? minX : maxX;

        double y = Math.abs( point.getY() - minY ) < Math.abs( point.getY() - maxY )
                   ? minY : maxY;
        closestCornerResult.setX( (float)x );
        closestCornerResult.setY( (float)y );
        return closestCornerResult;
    }

    /**
     * @param vx
     */
    public void setLeftWallVelocity( float vx ) {
        synchronized( leftWallMonitor ) {
            leftWallVx = vx;
        }
    }

    // TODO: change references so these methods don't have to be public.
    public float getCorner1X() {
        return (float)corner1.getX();
    }

    public float getCorner1Y() {
        return (float)corner1.getY();
    }

    public float getCorner2X() {
        return (float)corner2.getX();
    }

    public float getCorner2Y() {
        return (float)corner2.getY();
    }

    public Point2D getCenter() {
        return center;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public Wall[] getWalls() {
        return walls;
    }

//    public float getContactOffset( Body body ) {
//        return 0;
//    }

}
