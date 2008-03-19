/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class AtomicNucleus {
    
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    public static final double RADIUS = 5.5; // In femtometers
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // List of registered listeners.
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of the center of this nucleus.
    private Point2D.Double position;
    
    // List of the constituent particles that comprise this nucleus.
    private ArrayList _constituents = new ArrayList();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AtomicNucleus(double xPos, double yPos, int atomicNumber)
    {
        // Set the initial position for this nucleus.
        position = new Point2D.Double(xPos, yPos);
        
        // Figure out the proportions of various particles based on the atomic number.
        int numAlphas   = (atomicNumber / 2) / 4;  // Assume half of all particles are tied up in alphas.
        int numProtons = (atomicNumber / 4);
        int numNeutrons  = atomicNumber - numProtons - (numAlphas * 4);

        // Add the alpha particles.
        for (int i = 0; i < numAlphas; i++){
            _constituents.add( new AlphaParticle(0, 0) );
        }
        
        // Add the neutrons.
        for (int i = 0; i < numNeutrons; i++){
            _constituents.add( new Neutron(0, 0) );
        }

        // Add the protons.
        for (int i = 0; i < numProtons; i++){
            _constituents.add( new Proton(0, 0) );
        }
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------

    public Point2D getPosition(){
        return new Point2D.Double(position.getX(), position.getY());
    }
    
    public ArrayList getConstituents(){
        return _constituents;
    }
    
    //------------------------------------------------------------------------
    // Other methods
    //------------------------------------------------------------------------
    
    /**
     * This method lets this model element know that the clock has ticked.  In
     * response, the nucleus generally 'agitates' a bit.
     */
    public void clockTicked()
    {
        // Move each of the constituent particles to create the visual effect
        // of a very dynamic nucleus.
        for (int i = 0; i < _constituents.size(); i++)
        {
            AtomicNucleusConstituent constituent = (AtomicNucleusConstituent)_constituents.get( i );
            constituent.tunnel( 0, RADIUS );
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
