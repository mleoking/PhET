/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *  Viewport describes a rectangular portion of the model that is visible. 
 *  
 *  @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Viewport {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String _id;
    private Rectangle2D _bounds;
    private final ArrayList _listeners; // list of ViewportListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Viewport( String id ) {
        this( id, new Rectangle2D.Double( 0, 0, 0, 0 ) );
    }
    
    public Viewport( String id, Rectangle2D bounds ) {
        _id = id;
        _bounds = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
        _listeners = new ArrayList();
    }
    
    public void cleanup() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setBounds( double x, double y, double w, double h ) {
        if ( x != _bounds.getX() || y != _bounds.getY() || w != _bounds.getWidth() || h != _bounds.getHeight() ) {
            _bounds.setRect( x, y, w, h );
            notifyBoundsChanged();
        }
    }
    
    public void setBounds( Rectangle2D bounds ) {
        setBounds( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
    }
    
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double( _bounds.getX(), _bounds.getY(), _bounds.getWidth(), _bounds.getHeight() );
    }
    
    public Rectangle2D getBoundsReference() {
        return _bounds;
    }
    
    public double getX() {
        return _bounds.getX();
    }
    
    public double getY() {
        return _bounds.getY();
    }
    
    public double getWidth() {
        return _bounds.getWidth();
    }
    
    public double getHeight() {
        return _bounds.getHeight();
    }
    
    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }
    
    public void setPosition( double x, double y ) {
        setBounds( x, y, _bounds.getWidth(), _bounds.getHeight() );
    }
    
    public Point2D getPosition() {
        return new Point2D.Double( _bounds.getX(), _bounds.getY() );
    }
    
    public void translate( double dx, double dy ) {
        setPosition( _bounds.getX() + dx, _bounds.getY() + dy );
    }
    
    public void setSize( double w, double h ) {
        setBounds( _bounds.getX(), _bounds.getY(), w, h );
    }
    
    public String toString() {
        return getClass().getName() + "[id=" + _id + ",bounds=" + _bounds.toString() + "]";
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    /* Implement this interface to be notified of changes to a viewport. */
    public interface ViewportListener {
        public void boundsChanged();
    }
    
    public void addViewportListener( ViewportListener listener ) {
        _listeners.add( listener );
    }

    public void removeViewportListener( ViewportListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification of changes
    //----------------------------------------------------------------------------
    
    private void notifyBoundsChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewportListener) i.next() ).boundsChanged();
        }
    }
}
