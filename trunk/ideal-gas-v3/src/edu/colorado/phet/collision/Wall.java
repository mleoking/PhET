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

    public Wall( Rectangle2D bounds ) {
        this.rep = bounds;
        setMass( Double.POSITIVE_INFINITY );
        setPosition( bounds.getMinX(), bounds.getMinY() );
    }

    public void setPosition( double x, double y ) {
        if( x != getPosition().getX() || y != getPosition().getY() ) {
            super.setPosition( x, y );
            rep.setRect( x, y, rep.getWidth(), rep.getHeight() );
            changeListenerProxy.wallChanged( new ChangeEvent( this ) );
        }
    }

    public void setPosition( Point2D point ) {
        setPosition( point.getX(), point.getY() );
    }

    public Rectangle2D getBounds() {
        return rep;
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

    public void setMaxX( double x ) {
        rep.setRect( rep.getMinX(), rep.getMinY(), x - rep.getMinX(), rep.getHeight() );
        setPosition( rep.getMinX(), rep.getMinY() );
    }

    public void setMinX( double x ) {
        rep.setRect( x, rep.getMinY(), rep.getMaxX() - x, rep.getHeight() );
        setPosition( rep.getMinX(), rep.getMinY() );
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
