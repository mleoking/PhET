/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;


/**
 * Class the implements the behavior of nucleon (i.e. proton and neutron)
 * model elements.
 *
 * @author John Blanco
 */
public class Nucleon extends SubatomicParticle {
	
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	// Possible types of nucleons.  Not done as subclasses since they can
	// change into one another.
	public enum NucleonType {PROTON, NEUTRON};

	// Distance used for jittering the nucleons.
	private static final double JITTER_DISTANCE = NuclearPhysicsConstants.NUCLEON_DIAMETER * 0.1;
	
    // Random number generator, used for creating some random behavior.
    private static final Random RAND = new Random();

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    // Type of nucleon
    private NucleonType _type;
    
    // Boolean that controls whether this particle should exhibit quantum
    // tunneling behavior.
    private boolean _tunnelingEnabled;
    
    // Current jitter offset, used to create a vibrating motion effect.
    private final Point2D.Double _jitterOffset = new Point2D.Double();
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Construct a nucleon that is not moving.
     * @param nucleonType - Type of nucleon, either PROTON or NEUTRON.
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     * @param tunnelingEnabled - Controls whether this particle should exhibit
     * quantum tunneling behavior. 
     */
    public Nucleon(NucleonType nucleonType, double xPos, double yPos, boolean tunnelingEnabled)
    {
        this(nucleonType, xPos, yPos, 0, 0, tunnelingEnabled);
    }
    
    /**
     * Construct a nucleon.
     * @param nucleonType - Type of nucleon, either PROTON or NEUTRON.
     * @param xPos - Initial X position of this particle.
     * @param yPos - Initial Y position of this particle.
     * @param xVel - Initial velocity in the X direction.
     * @param yVel - Initial velocity in the Y direction.
     * @param tunnelingEnabled - Controls whether this particle should exhibit
     */
    public Nucleon(NucleonType nucleonType, double xPos, double yPos, double xVel, double yVel, boolean tunnelingEnabled){
    	super(xPos, yPos, xVel, yVel);
    	_type = nucleonType;
        _tunnelingEnabled = tunnelingEnabled;
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public void setTunnelingEnabled(boolean tunnelingEnabled){
        _tunnelingEnabled = tunnelingEnabled;
    }
    
    public boolean getTunnelingEnabled(){
        return _tunnelingEnabled;
    }
    
    public void setNucleonType(NucleonType nucleonType){
    	if (_type != nucleonType){
    		_type = nucleonType;
    		notifyNucleonTypeChanged();
    	}
    }
    
    public NucleonType getNucleonType(){
    	return _type;
    }
        
    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------
    
	/**
     * This method simulates the quantum tunneling behavior, which means that
     * it causes the particle to move to some new random location within the
     * confines of the supplied parameters.
     * 
     * @param minDistance - Minimum distance from origin (0,0).  This is
     * generally 0.
     * @param nucleusRadius - Radius of the nucleus where this particle resides.
     * @param tunnelRadius - Radius at which this particle could tunnel out of nucleus.
     */
    public void tunnel(Point2D center, double minDistance, double nucleusRadius, double tunnelRadius)
    {
        if (_tunnelingEnabled){
            
            // Create a probability distribution that will cause the particles to
            // be fairly evenly spread around the core of the nucleus and appear
            // occasionally at the outer reaches.
    
            double multiplier = RAND.nextDouble();
            
            if (multiplier > 0.8){
                // Cause the distribution to tail off in the outer regions of the
                // nucleus.
                multiplier = RAND.nextDouble() * RAND.nextDouble();
            }
            
            double newRadius = minDistance + (multiplier * (nucleusRadius - minDistance));
            
            // Calculate the new angle, in radians, from the origin.
            double newAngle = RAND.nextDouble() * 2 * Math.PI;
            
            // Convert from polar to Cartesian coordinates.
            double xPos = Math.cos( newAngle ) * newRadius;
            double yPos = Math.sin( newAngle ) * newRadius;
            
            // Save the new position.
            setPosition( xPos + center.getX(), yPos + center.getY());
        }
    }
    
    public void jitter(){
    	if (_jitterOffset.getX() == 0 && _jitterOffset.getY() == 0){
    		// Move away from the base position by a small amount.
            double angle = RAND.nextDouble() * 2 * Math.PI;
            _jitterOffset.setLocation( Math.cos( angle ) * JITTER_DISTANCE, Math.sin( angle ) * JITTER_DISTANCE);
            setPosition(getPosition().x + _jitterOffset.x, getPosition().y + _jitterOffset.y);
    	}
    	else{
    		// Move back to the base position.
            setPosition(getPosition().x - _jitterOffset.x, getPosition().y - _jitterOffset.y);
            _jitterOffset.setLocation(0, 0);
    	}
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    protected void notifyNucleonTypeChanged(){
        // Notify all listeners that are able to receive this message that the
    	// nucleon type has changed.
        for (Listener listener : _listeners)
        {
        	if (listener instanceof NucleonListener){
        		((NucleonListener)listener).nucleonTypeChanged();
        	}
        }        
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
    
    public static interface NucleonListener extends SubatomicParticle.Listener{
    	public void nucleonTypeChanged();
    }
    
    public static class NucleonAdapter implements NucleonListener{
		public void nucleonTypeChanged() {}
		public void positionChanged(SubatomicParticle particle) {}
    }
}
