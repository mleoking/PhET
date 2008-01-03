/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * World describes general characteristics of the world.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class World {
    
    private Rectangle2D _bounds;
    private ArrayList _listeners;

    public World() {
        this( new Rectangle2D.Double() );
    }
    
    public World( Rectangle2D bounds ) {
        _bounds = new Rectangle2D.Double();
        _bounds.setRect( bounds );
        _listeners = new ArrayList();
    }
    
    public void setBounds( Rectangle2D bounds ) {
        if ( !bounds.equals( _bounds ) ) {
            System.out.println( "World.setBounds bounds=" + bounds );//XXX
            _bounds.setRect( bounds );
            notifyBoundsChanged();
        }
    }
    
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double( _bounds.getX(), _bounds.getY(), _bounds.getWidth(), _bounds.getHeight() );
    }
    
    public Rectangle2D getBoundsReference() {
        return _bounds;
    }
    
    public boolean contains( Rectangle2D r ) {
        return _bounds.contains( r );
    }
    
    public interface WorldListener {
        public void boundsChanged();
    }
    
    public void addListener( WorldListener listener ) {
        _listeners.add( listener );
    }

    public void removeListener( WorldListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyBoundsChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            Object listener = _listeners.get( i );
            if ( listener instanceof WorldListener ) {
                ( (WorldListener) listener ).boundsChanged();
            }
        }
    }
}
