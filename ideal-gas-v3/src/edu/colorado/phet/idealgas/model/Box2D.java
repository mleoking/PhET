/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.CollidableBody;
import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;


/**
 * A 2 dimensional box
 */
public class Box2D extends CollidableBody {

    private Point2D corner1;
    private Point2D corner2;
    private Point2D center;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private double leftWallVx = 0;
    boolean autoNotify = true;


    // TODO: put the opening characteristics in a specialization of this class.
    private Point2D[] opening = new Point2D.Double[]{
        new Point2D.Double(),
        new Point2D.Double()};

    private IdealGasModel model;
    private double oldMinX;
    private double minimumWidth = 100;

    public Box2D( IdealGasModel model ) {
        super();
        this.model = model;
        setMass( Double.POSITIVE_INFINITY );
    }

    public Box2D( Point2D corner1, Point2D corner2, IdealGasModel model ) {
        super();
        this.model = model;
        this.setState( corner1, corner2 );
        oldMinX = Math.min( corner1.getX(), corner2.getX() );
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
        changeListenerProxy.boundsChanged( new ChangeEvent( this ) );
    }

    public void notifyObservers() {
        if( autoNotify ) {
            super.notifyObservers();
        }
    }

    private void setState( Point2D corner1, Point2D corner2 ) {
        setAutoNotify( false );
        this.corner1 = corner1;
        this.corner2 = corner2;
        maxX = Math.max( corner1.getX(), corner2.getX() );
        maxY = Math.max( corner1.getY(), corner2.getY() );
        minX = Math.max( Math.min( Math.min( corner1.getX(), corner2.getX() ), maxX - minimumWidth ), 40 );
        minY = Math.min( corner1.getY(), corner2.getY() );
        center = new Point2D.Double( ( this.maxX + this.minX ) / 2,
                                     ( this.maxY + this.minY ) / 2 );
        setPosition( new Point2D.Double( minX, minY ) );

        // Update the position of the door
        Point2D[] opening = this.getOpening();
        opening[0].setLocation( opening[0].getX(), minY );
        opening[1].setLocation( opening[1].getX(), minY );
        this.setOpening( opening );
        setAutoNotify( true );
        this.notifyObservers();
    }

    private void setAutoNotify( boolean b ) {
        this.autoNotify = b;
    }

    public void setMinimumWidth( double minimumWidth ) {
        this.minimumWidth = minimumWidth;
    }

    public double getMinimumWidth() {
        return minimumWidth;
    }

    public void setOpening( Point2D[] opening ) {
        this.opening[0] = opening[0];
        this.opening[1] = opening[1];
        notifyObservers();
    }

    public Point2D[] getOpening() {
        return this.opening;
    }

    public boolean isInOpening( CollidableBody body ) {
        boolean result = false;
        if( body instanceof SphericalBody ) {
            SphericalBody particle = (SphericalBody)body;
            if( particle.getPosition().getX() >= this.opening[0].getX()
                && particle.getPosition().getX() <= this.opening[1].getX()
                && particle.getPosition().getY() - particle.getRadius() <= this.getMinY() ) {
                result = true;
            }
            else {
                result = false;
            }
        }
        return result;
    }


    public void stepInTime( double dt ) {
        // Compute the speed of the left wall
        leftWallVx = ( minX - oldMinX ) / dt;
        oldMinX = minX;
    }

    //    private boolean containsBody( SphericalBody particle ) {
    //        super.containsBody( )
    //        return getContainedBodies().contains( particle );
    ////        return containedBodies.contains( particle );
    //    }

    /**
     *
     */
    public boolean isOutsideBox( SphericalBody particle ) {
        Point2D p = particle.getPosition();
        double rad = particle.getRadius();
        boolean isInBox = p.getX() - rad >= this.getMinX()
                          && p.getX() + rad <= this.getMaxX()
                          && p.getY() - rad >= this.getMinY()
                          && p.getY() + rad <= this.getMaxY();
        return !isInBox;
    }

    public Rectangle2D getBoundsInternal() {
        return new Rectangle2D.Double( getCorner1X(), getCorner1Y(), getWidth(), getHeight() );
    }

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

    public float getContactOffset( CollidableBody body ) {
        return 0;
    }

    public double getLeftWallVx() {
        return leftWallVx;
    }

    //    public void addContainedBody( Body body ) {
    //        addContainedBody( body );
    //    }


    //-----------------------------------------------------------------
    // Events and event handling
    //-----------------------------------------------------------------
    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public Box2D getBox2D() {
            return (Box2D)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void boundsChanged( ChangeEvent event );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
