/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.idealgas.coreadditions.Translatable;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Wall
 * <p/>
 * A model element representing a wall that other model elements can collide with. The
 * wall's position and size are constrained by bounds specified by the client.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

public class Wall extends CollidableBody implements Translatable {
    private Vector2D velocity = new Vector2D.Double();
    private Rectangle2D rep = new Rectangle2D.Double();
    private Rectangle2D movementBounds;
    private Rectangle2D prevRep = new Rectangle2D.Double();
    private double minimumWallThickness;
    private WallFixupStrategy fixupStrategy = new NullFixupStrategy();

    /**
     * @param bounds
     * @param movementBounds
     */
    public Wall( Rectangle2D bounds, Rectangle2D movementBounds ) {
        this.rep = bounds;
        this.movementBounds = movementBounds;
        setMass( Double.POSITIVE_INFINITY );
        setPosition( bounds.getMinX(), bounds.getMinY() );
    }

    /**
     * Does nothing. This is so the wall is not affected by gravity
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        // noop
    }

    /**
     * Will not position the wall outside its movement bounds
     *
     * @param x
     * @param y
     */
    public void setPosition( double x, double y ) {

        // Constrain the position to be within the movement bounds
        x = Math.min( x, movementBounds.getMaxX() - rep.getWidth() );
        x = Math.max( x, movementBounds.getMinX() );
        y = Math.min( y, movementBounds.getMaxY() - rep.getHeight() );
        y = Math.max( y, movementBounds.getMinY() );

        super.setPosition( x, y );
        prevRep.setRect( rep );
        rep.setRect( x, y, rep.getWidth(), rep.getHeight() );
        changeListenerProxy.wallChanged( new ChangeEvent( this ) );
    }

    /**
     * Will not position the wall outside its movement bounds
     *
     * @param point
     */
    public void setPosition( Point2D point ) {
        setPosition( point.getX(), point.getY() );
    }

    /**
     * Will not expand the wall outside of its movement bounds, and maintains the walls minimum dimensions
     *
     * @param bounds
     */
    public void setBounds( Rectangle2D bounds ) {

        if( bounds.getWidth() < this.minimumWallThickness ) {
            return;
        }

        // Constrain the wall to be within the movement bounds, and maintain minimum dimensions
        double minX = Math.max( Math.min( bounds.getMinX(), movementBounds.getMaxX() ), movementBounds.getMinX() );
        double minY = Math.max( Math.min( bounds.getMinY(), movementBounds.getMaxY() ), movementBounds.getMinY() );
        double maxX = Math.min( Math.max( bounds.getMaxX(), movementBounds.getMinX() ), movementBounds.getMaxX() );
        double maxY = Math.min( Math.max( bounds.getMaxY(), movementBounds.getMinY() ), movementBounds.getMaxY() );

        rep.setRect( minX, minY, maxX - minX, maxY - minY );
        setPosition( minX, minY );
    }

    //I just changed this to return a new object
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double( rep.getX(), rep.getY(), rep.getWidth(), rep.getHeight() );
    }

    public Rectangle2D getPrevBounds() {
        return new Rectangle2D.Double( prevRep.getX(), prevRep.getY(), prevRep.getWidth(), prevRep.getHeight() );
    }

    public void setMovementBounds( Rectangle2D movementBounds ) {
        this.movementBounds = movementBounds;
        changeListenerProxy.wallChanged( new ChangeEvent( this ) );
    }

    public Rectangle2D getMovementBounds() {
        return movementBounds;
    }

    public Vector2D getVelocityPrev() {
        return velocity;
    }

    public Point2D getPositionPrev() {
        return null;
    }

    public Point2D getCM() {
        return new Point2D.Double( rep.getMinX() + rep.getWidth() / 2,
                                   rep.getMinY() + rep.getHeight() / 2 );
    }

    public double getMomentOfInertia() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Since the wall is infinitely massive, it can't move, and so
     * we say its kinetic energy is 0
     */
    public double getKineticEnergy() {
        return 0;
    }

    public void translate( double dx, double dy ) {
        super.translate( dx, dy );
    }

    //----------------------------------------------------------------
    // Code for fixing up collision anomolies
    //----------------------------------------------------------------
    public void setFixupStrategy( WallFixupStrategy fixupStrategy ) {
        this.fixupStrategy = fixupStrategy;
    }

    public void fixup( SphericalBody sphere ) {
        fixupStrategy.fixup( this, sphere );
    }

    //----------------------------------------------------------------
    // Event-related data, classes, and methods
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public void setMinimumWidth( double wallThickness ) {
        this.minimumWallThickness = wallThickness;
    }

    public interface ChangeListener extends EventListener {
        void wallChanged( ChangeEvent event );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public Wall getWall() {
            return (Wall)getSource();
        }
    }
}
