/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class AtomicNucleus {
    
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of this atom.
    private Point2D.Double position;
    
    public AtomicNucleus(double xPos, double yPos)
    {
        position = new Point2D.Double(xPos, yPos);
    }
    
    public Point2D getPosition()
    {
        return new Point2D.Double(position.getX(), position.getY());
    }
    
    public void translate(double dx, double dy)
    {
        position.x += dx;
        position.y += dy;
        
        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(); 
        }
    }
    
    public void addListener(Listener listener)
    {
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    public static interface Listener {
        void positionChanged();
    }
}
