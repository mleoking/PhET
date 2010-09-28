package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class SubatomicParticle {
    private final Point2D.Double position;
    private final ArrayList<Listener> listeners = new ArrayList<Listener>();
    private final double radius;

    public SubatomicParticle( double radius, double x, double y ) {
        this.radius = radius;
        position = new Point2D.Double( x, y );
    }

    public Point2D.Double getPosition() {
        return new Point2D.Double( position.getX(), position.getY() );
    }

    public void setPosition( Point2D.Double position ) {
        setPosition( position.x, position.y );
    }

    public void setPosition( double x, double y ) {
        this.position.setLocation( x, y );
    }
    public double getDiameter() {
        return getRadius() * 2;
    }
    private double getRadius() {
        return radius;
    }
    public static interface Listener {
        void positionChanged();
    }
    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }
}
