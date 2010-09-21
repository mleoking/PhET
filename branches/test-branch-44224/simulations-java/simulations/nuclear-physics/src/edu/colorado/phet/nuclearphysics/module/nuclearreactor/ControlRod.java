/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.nuclearreactor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics.common.model.Nucleon;

/**
 * This class represents the position and behavior of the control rods within
 * a model of a nuclear reactor.
 *
 * @author John Blanco
 */
public class ControlRod {

    //-----------------------------------------------------------------------------
    // Class Data
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // Instance Data
    //-----------------------------------------------------------------------------

    Rectangle2D _rect;
    
    // List of registered listeners.
    ArrayList _listeners;
    
    //-----------------------------------------------------------------------------
    // Constructor(s)
    //-----------------------------------------------------------------------------

    ControlRod(double xPos, double yPos, double width, double height){

        // Initialize local data.
        _listeners = new ArrayList();
        
        // Set up the rectangle that defines our position in space.
        _rect = new Rectangle2D.Double(xPos, yPos, width, height);
    }
    
    //-----------------------------------------------------------------------------
    // Accessor Methods
    //-----------------------------------------------------------------------------

    public Rectangle2D getRectangleReference(){
        return _rect;
    }
    
    /**
     * Set the position of this control rod.  Note that control rods can only
     * move up and down after they are created, so only the Y position can be
     * set.
     * 
     * @param yPos - Desired vertical position for this control rod.
     */
    public void setPosition(double yPos){
        if (yPos != _rect.getY()){
            _rect.setRect(_rect.getX(), yPos, _rect.getWidth(), _rect.getHeight());
            notifyPositionChanged();
        }
    }
    
    /**
     * Get a point representing the position of the upper left point of the
     * control rod.
     * 
     * @return
     */
    public Point2D getPosition(){
        return new Point2D.Double(_rect.getX(), _rect.getY());
    }

    //-----------------------------------------------------------------------------
    // Other Public Methods
    //-----------------------------------------------------------------------------

    /**
     * Returns true if the particle can be absorbed by the control rod, false
     * if not.
     */
    public boolean particleAbsorbed(Nucleon particle){
        return (_rect.contains( particle.getPosition() ));
    }

    //-----------------------------------------------------------------------------
    // Private Methods
    //-----------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

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
    
    private void notifyPositionChanged(){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get(i)).positionChanged();
        }
    }
}
