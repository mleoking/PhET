/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics2.model.AlphaParticle.Listener;

/**
 * This class models the behavior of a neutron source, i.e. some sort of
 * device that can generate neutrons.
 *
 * @author John Blanco
 */
public class NeutronSource {
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of this particle.
    private Point2D.Double _position;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NeutronSource(double xPos, double yPos)
    {
        _position = new Point2D.Double(xPos, yPos);
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public Point2D getPosition(){
        return new Point2D.Double(_position.getX(), _position.getY());
    }

    //------------------------------------------------------------------------
    // Listener Support
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
        void neutronCreated(Neutron newNeutron);
    }
}
