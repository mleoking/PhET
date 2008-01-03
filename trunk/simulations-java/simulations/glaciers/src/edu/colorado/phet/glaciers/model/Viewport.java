/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.glaciers.model.World.WorldListener;

/**
 *  Viewport describes a portion of the World that is visible. 
 *  
 *  @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Viewport {
    
    /* Implement this interface to be notified of changes to a viewport. */
    public interface ViewportListener {
        public void boundsChanged();
    }
    
    private World _world;
    private Rectangle2D _bounds;
    private ArrayList _listeners;
    
    public Viewport( World world ) {
        this( world, new Rectangle2D.Double() );
    }
    
    public Viewport( World world, Rectangle2D bounds ) {
        _world = world;
        _bounds = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
        _listeners = new ArrayList();
    }
    
    public void setBounds( Rectangle2D bounds ) {
        if ( !_world.contains( bounds ) ) {
            System.err.println( "Viewport.setBounds: ignoring bounds, they are outside the world, bounds=" + bounds + " world=" + _world.getBoundsReference() );
        }
        else if ( !bounds.equals( _bounds ) ) {
            _bounds.setRect( bounds );
            System.out.println( "Viewport.setBounds bounds=" + bounds );//XXX
            notifyBoundsChanged();
        }
    }
    
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double( _bounds.getX(), _bounds.getY(), _bounds.getWidth(), _bounds.getHeight() );
    }
    
    public Rectangle2D getBoundsReference() {
        return _bounds;
    }
    
    public void addListener( ViewportListener listener ) {
        _listeners.add( listener );
    }

    public void removeListener( ViewportListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyBoundsChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            Object listener = _listeners.get( i );
            if ( listener instanceof ViewportListener ) {
                ( (ViewportListener) listener ).boundsChanged();
            }
        }
    }
}
