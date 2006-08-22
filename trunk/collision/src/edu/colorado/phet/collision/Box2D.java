/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A 2 dimensional box
 */
public class Box2D extends Body implements Collidable {

    private Point2D corner1;
    private Point2D corner2;
    private Point2D center;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private double leftWallVx = 0;
    private double minimumWidth = 100;
    private CollidableAdapter collidableAdapter = new CollidableAdapter( this );
    private Rectangle2D.Double bounds = new Rectangle2D.Double();
    // This is something that is probably left over from the far past of the Ideal Gas sim.
    // Rather than break things, I've added it as a constructor parameter so you can make it 0.
    // See setState()
    private double width0 = 40;

    public Box2D() {
        this( new Point2D.Double(), new Point2D.Double() );
    }

    public Box2D( Point2D corner1, Point2D corner2 ) {
        super();
        this.setState( corner1, corner2 );
        setMass( Double.POSITIVE_INFINITY );
    }

    public Box2D( Point2D corner1, Point2D corner2, double width0 ) {
        super();
        this.width0 = width0;
        this.setState( corner1, corner2 );
        setMass( Double.POSITIVE_INFINITY );
    }

    /**
     * Since the box is infinitely massive, it can't move, and so
     * we say its kinetic energy is 0
     */
    public double getKineticEnergy() {
        return 0;
    }

    public Point2D getCM() {
        return center;
    }

    public double getMomentOfInertia() {
        return Double.MAX_VALUE;
    }

    public void setBounds( double minX, double minY, double maxX, double maxY ) {
        this.setState( new Point2D.Double( minX, minY ), new Point2D.Double( maxX, maxY ) );
    }

    public Rectangle2D getBounds() {
        bounds.setRect( minX, minY, getWidth(), getHeight() );
        return bounds;
    }

    private void setState( Point2D corner1, Point2D corner2 ) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        maxX = Math.max( corner1.getX(), corner2.getX() );
        maxY = Math.max( corner1.getY(), corner2.getY() );
        minX = Math.max( Math.min( Math.min( corner1.getX(), corner2.getX() ), maxX - minimumWidth ), width0 );
        minY = Math.min( corner1.getY(), corner2.getY() );
        center = new Point2D.Double( ( this.maxX + this.minX ) / 2,
                                     ( this.maxY + this.minY ) / 2 );
        setPosition( new Point2D.Double( minX, minY ) );

        this.notifyObservers();
    }

    public void setMinimumWidth( double minimumWidth ) {
        this.minimumWidth = minimumWidth;
    }

    public double getMinimumWidth() {
        return minimumWidth;
    }

//    public void stepInTime( double DT ) {
//    }

//    private boolean containsBody( SphericalBody particle ) {
//        super.containsBody( )
//        return getContainedBodies().contains( particle );
////        return containedBodies.contains( particle );
//    }

    /**
     *
     */
//    public boolean isOutsideBox( SphericalBody particle ) {
//        Point2D p = particle.getPosition();
//        double rad = particle.getRadius();
//        boolean isInBox = p.getX() - rad >= this.getMinX()
//                          && p.getX() + rad <= this.getMaxX()
//                          && p.getY() - rad >= this.getMinY()
//                          && p.getY() + rad <= this.getMaxY();
//        return !isInBox;
//    }

    // TODO: change references so these methods don't have to be public.
    public double getCorner1X() {
        return corner1.getX();
    }

    public double getCorner1Y() {
        return corner1.getY();
    }

    public double getCorner2X() {
        return corner2.getX();
    }

    public double getCorner2Y() {
        return corner2.getY();
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

    public double getWidth() {
        return Math.abs( corner2.getX() - corner1.getX() );
    }

    public double getHeight() {
        return Math.abs( corner2.getY() - corner1.getY() );
    }

    public double getContactOffset( Body body ) {
        return 0;
    }

    public double getLeftWallVx() {
        return leftWallVx;
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }
}
