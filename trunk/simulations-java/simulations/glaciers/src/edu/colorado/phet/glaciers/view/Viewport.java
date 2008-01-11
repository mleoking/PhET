/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *  Viewport describes the portion of the scene that's visible through the viewport,
 *  and displayed in the "zoomed" view. 
 *  
 *  @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Viewport {
    
    private String _id;
    private Rectangle2D _bounds;
    private ArrayList _listeners; // list of ViewportListener
    
    public Viewport( String id ) {
        this( id, new Rectangle2D.Double() );
    }
    
    public Viewport( String id, Rectangle2D bounds ) {
        _id = id;
        _bounds = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
        _listeners = new ArrayList();
    }
    
    public void cleanup() {}
    
    public void setBounds( Rectangle2D bounds ) {
        if ( !bounds.equals( _bounds ) ) {
            System.out.println( "Viewport.setBounds id=" + _id + " bounds=" + bounds );//XXX
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
    
    public void translate( double dx, double dy ) {
        if ( dx !=0 || dy != 0 ) {
            setBounds( new Rectangle2D.Double( _bounds.getX() + dx, _bounds.getY() + dy, _bounds.getWidth(), _bounds.getHeight() ) );
        }
    }
    
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
    
    private void notifyBoundsChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewportListener) i.next() ).boundsChanged();
        }
    }
}
