/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Wall
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

public class Wall extends CollidableBody {
    private Vector2D velocity = new Vector2D.Double();
    private Rectangle2D rep = new Rectangle2D.Double();
    private Rectangle2D movementBounds;
    private Rectangle2D prevRep = new Rectangle2D.Double();

    public Wall( Rectangle2D bounds, Rectangle2D movementBounds ) {
        this.rep = bounds;
        this.movementBounds = movementBounds;
        setMass( Double.POSITIVE_INFINITY );
        setPosition( bounds.getMinX(), bounds.getMinY() );
    }

    public void setPosition( double x, double y ) {

        x = Math.min( x, movementBounds.getMaxX() - rep.getWidth() );
        x = Math.max( x, movementBounds.getMinX() );
        y = Math.min( y, movementBounds.getMaxY() - rep.getHeight() );
        y = Math.max( y, movementBounds.getMinY() );
        if( x != getPosition().getX() || y != getPosition().getY() ) {
            super.setPosition( x, y );
            prevRep.setRect( rep );
            rep.setRect( x, y, rep.getWidth(), rep.getHeight() );
            changeListenerProxy.wallChanged( new ChangeEvent( this ) );
        }
    }

    public void setPosition( Point2D point ) {
        setPosition( point.getX(), point.getY() );
    }

    public void setBounds( Rectangle2D bounds ) {
        rep.setRect( bounds );
        changeListenerProxy.wallChanged( new ChangeEvent( this ) );
    }

    public Rectangle2D getBounds() {
        return rep;
    }

    public Rectangle2D getPrevBounds() {
        return prevRep;
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
