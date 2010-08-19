/* Copyright 2009, University of Colorado */
package edu.colorado.phet.membranediffusion.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class represents the main class for modeling membrane diffusion.  It
 * acts as the central location where the interactions between the membrane,
 * the particles (i.e. ions), and the gates are all governed.
 *
 * @author John Blanco
 */
public class MembraneDiffusionModel implements IParticleCapture {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	// Definition of the rectangle where the particles can move.  Note that
	// the center of the chamber is assumed to be at (0,0).
	private static final double PARTICLE_CHAMBER_WIDTH = 70; // In nanometers.
	private static final double PARTICLE_CHAMBER_HEIGHT = 60; // In nanometers.
	private static final Rectangle2D PARTICLE_CHAMBER_RECT = new Rectangle2D.Double( -PARTICLE_CHAMBER_WIDTH / 2,
			-PARTICLE_CHAMBER_HEIGHT / 2, PARTICLE_CHAMBER_WIDTH, PARTICLE_CHAMBER_HEIGHT );
	
	// Definition of the rectangle that separates the upper and lower portions
	// of the chamber.
	private static final double MEMBRANE_THICKNESS = 4; // In nanometers.
	private static final Rectangle2D MEMBRANE_RECT = new Rectangle2D.Double( -PARTICLE_CHAMBER_WIDTH / 2,
			-MEMBRANE_THICKNESS / 2, PARTICLE_CHAMBER_WIDTH, MEMBRANE_THICKNESS );
	
	// Maximum number of channels allowed on the membrane.
	private static final int MAX_CHANNELS_ON_MEMBRANE = 10;
	
	// Max number of particles allowed.
	public static final int MAX_PARTICLES = 100;
	
	// Defaults for configurable parameters.
	private static boolean SHOW_GRAPHS_DEFAULT = false;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final ConstantDtClock clock;
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private ArrayList<MembraneChannel> membraneChannels = new ArrayList<MembraneChannel>();
    private MembraneChannel userControlledMembraneChannel = null;
    private EventListenerList listeners = new EventListenerList();
    private FakeHodgkinHuxleyModel hodgkinHuxleyModel = new FakeHodgkinHuxleyModel();
    private final ArrayList<Point2D> allowableChannelLocations = new ArrayList<Point2D>(MAX_CHANNELS_ON_MEMBRANE);
    private boolean concentrationGraphsVisible = SHOW_GRAPHS_DEFAULT;
    
    // Counts of particles in the subchambers.
    private int numSodiumInUpperChamber = 0;
    private int numPotassiumInUpperChamber = 0;
    private int numSodiumInLowerChamber = 0;
    private int numPotassiumInLowerChamber = 0;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
	public MembraneDiffusionModel( MembraneDiffusionClock clock ) {
    	
        this.clock = clock;
        
        clock.addClockListener(new ClockAdapter(){
			@Override
			public void clockTicked(ClockEvent clockEvent) {
				stepInTime( clockEvent.getSimulationTimeChange() );
			}
        });
        
        // Initialize the set of points where channels can be located.
        double interChannelDistance = MEMBRANE_RECT.getWidth() / (double)MAX_CHANNELS_ON_MEMBRANE;
        double channelLocationOffset = MEMBRANE_RECT.getMinX() + interChannelDistance / 2;
        for (int i = 0; i < MAX_CHANNELS_ON_MEMBRANE; i++){
        	allowableChannelLocations.add(new Point2D.Double(channelLocationOffset + i * interChannelDistance, MEMBRANE_RECT.getCenterY()));
        }
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    public static Rectangle2D getOverallParticleChamberRect(){
    	return new Rectangle2D.Double(PARTICLE_CHAMBER_RECT.getX(), PARTICLE_CHAMBER_RECT.getY(),
    			PARTICLE_CHAMBER_RECT.getWidth(), PARTICLE_CHAMBER_RECT.getHeight());
    }
    
    public Rectangle2D getUpperParticleChamberRect(){
        return new Rectangle2D.Double(PARTICLE_CHAMBER_RECT.getMinX(), MEMBRANE_RECT.getMaxY(),
                PARTICLE_CHAMBER_RECT.getWidth(), PARTICLE_CHAMBER_RECT.getMaxY() - MEMBRANE_RECT.getMaxY());
    }
    
    public Rectangle2D getLowerParticleChamberRect(){
        return new Rectangle2D.Double(PARTICLE_CHAMBER_RECT.getMinX(), PARTICLE_CHAMBER_RECT.getMinY(),
                PARTICLE_CHAMBER_RECT.getWidth(), MEMBRANE_RECT.getMinY() - PARTICLE_CHAMBER_RECT.getMinY());
    }
    
    public static Rectangle2D getMembraneRect(){
    	return new Rectangle2D.Double(MEMBRANE_RECT.getX(), MEMBRANE_RECT.getY(),
    			MEMBRANE_RECT.getWidth(), MEMBRANE_RECT.getHeight());
    }
    
    public static double getMembraneThickness(){
    	return MEMBRANE_RECT.getHeight();
    }
    
    public ConstantDtClock getClock() {
        return clock;
    }    
    
    public ArrayList<Particle> getParticles(){
    	return particles;
    }
    
    public ArrayList<MembraneChannel> getMembraneChannels(){
    	return new ArrayList<MembraneChannel>(membraneChannels);
    }
    
    public IHodgkinHuxleyModel getHodgkinHuxleyModel(){
    	return hodgkinHuxleyModel;
    }
    
    public boolean isConcentrationGraphsVisible() {
		return concentrationGraphsVisible;
	}

    public void setConcentrationGraphsVisible(boolean concentrationGraphsVisible) {
    	if ( this.concentrationGraphsVisible != concentrationGraphsVisible ){
    		this.concentrationGraphsVisible = concentrationGraphsVisible;
    		notifyConcentrationGraphVisibilityChanged();
    	}
	}
    
    public double getCountInUpperSubChamber(ParticleType particleType){
    	int count = 0;
    	switch (particleType){
    	case SODIUM_ION:
    		count = numSodiumInUpperChamber;
    		break;
    		
    	case POTASSIUM_ION:
    		count = numPotassiumInUpperChamber;
    		break;
    		
    	default:
    		System.out.println(getClass().getName() + "Error: Unrecognized particle type.");
    		assert false;
    		break;
    	}
    	
    	return count;
    }

    public double getCountInLowerSubChamber(ParticleType particleType){
    	int count = 0;
    	switch (particleType){
    	case SODIUM_ION:
    		count = numSodiumInLowerChamber;
    		break;
    		
    	case POTASSIUM_ION:
    		count = numPotassiumInLowerChamber;
    		break;
    		
    	default:
    		System.out.println(getClass().getName() + "Error: Unrecognized particle type.");
    		assert false;
    		break;
    	}
    	
    	return count;
    }

    public void reset(){
    	
    	// Reset the HH model.
    	hodgkinHuxleyModel.reset();
    	
    	// Remove the particles.
    	removeAllParticles();
    	
    	// Remove all membrane channels.
    	removeAllChannels();
    	
    	// Set the graphs to their default state.
    	setConcentrationGraphsVisible(SHOW_GRAPHS_DEFAULT);
    }
    
    public void forceActivationOfSodiumChannels(){
    	hodgkinHuxleyModel.forceActivationOfSodiumChannels();
    }
    
    public void forceActivationOfPotassiumChannels(){
    	hodgkinHuxleyModel.forceActivationOfPotassiumChannels();
    }
    
    /**
     * Inject a particle into either the upper or lower particle chamber.
     * 
     * @param particle - Assumed to have its motion strategy and location
     * already set.
     * @return true if able to add the particle, false if something prevents
     * the particle from being added.
     */
    public boolean injectParticle(final Particle particle){
        
    	// Validate that there are not already to many.
        if (particles.size() >= MAX_PARTICLES){
            System.err.println(getClass().getName() + " - Warning: Ignoring attempt to add more particles than allowed.");
            return false;
        }
    	
    	// Validate that the particle is in bounds.
    	if (!PARTICLE_CHAMBER_RECT.contains(particle.getPositionReference())){
            System.err.println(getClass().getName() + " - Warning: Ignoring attempt to add particle outside of chamber.");
    		return false;
    	}
    	
    	// Add the particle to the list and send appropriate notification.
    	particles.add(particle);
    	notifyParticleAdded(particle);
    	
    	// Listen for notifications from this particle that indicate that it
    	// is being removed from the model.
    	particle.addListener(new Particle.Adapter(){
    		public void removedFromModel() {
    			particles.remove(particle);
    		}
    	});
    	
    	// If we made it to this point, everything went okay.
    	return true;
    }
    
    public int getRemainingParticleCapacity(){
        return MAX_PARTICLES - particles.size();
    }
    
    private void stepInTime(double dt){
    
    	// Update the value of the membrane potential by stepping the
    	// Hodgkins-Huxley model.
    	hodgkinHuxleyModel.stepInTime( dt );
    	
    	// Step the channels.
    	for (MembraneChannel channel : membraneChannels){
    		channel.stepInTime( dt );
    	}
    	
    	// Step the particles.  Since particles may remove themselves as a
    	// result of being stepped, we need to copy the list in order to avoid
    	// concurrent modification exceptions.  Also update the concentration
    	// counts.
    	int currentNumSodiumInUpperChamber = 0;
    	int currentNumPotassiumInUpperChamber = 0;
    	int currentNumSodiumInLowerChamber = 0;
    	int currentNumPotassiumInLowerChamber = 0;
    	ArrayList<Particle> particlesCopy = new ArrayList<Particle>(particles);
    	for (Particle particle : particlesCopy){
    		particle.stepInTime( dt );
    		if (particle.getType() == ParticleType.SODIUM_ION){
    			if (particle.getPositionReference().getY() > MEMBRANE_RECT.getCenterY()){
    				currentNumSodiumInUpperChamber++;
    			}
    			else{
    				currentNumSodiumInLowerChamber++;
    			}
    		}
    		else if (particle.getType() == ParticleType.POTASSIUM_ION){
    			if (particle.getPositionReference().getY() > MEMBRANE_RECT.getCenterY()){
    				currentNumPotassiumInUpperChamber++;
    			}
    			else{
    				currentNumPotassiumInLowerChamber++;
    			}
    		}
    		else{
    			System.out.println(getClass().getName() + " - Error: Unrecognized particle type.");
    		}
    	}
    	
    	boolean concentrationsChanged = false;
    	if (currentNumSodiumInUpperChamber != numSodiumInUpperChamber){
    		numSodiumInUpperChamber = currentNumSodiumInUpperChamber;
    		concentrationsChanged = true;
    	}
    	if (currentNumSodiumInLowerChamber != numSodiumInLowerChamber){
    		numSodiumInLowerChamber = currentNumSodiumInLowerChamber;
    		concentrationsChanged = true;
    	}
    	if (currentNumPotassiumInUpperChamber != numPotassiumInUpperChamber){
    		numPotassiumInUpperChamber = currentNumPotassiumInUpperChamber;
    		concentrationsChanged = true;
    	}
    	if (currentNumPotassiumInLowerChamber != numPotassiumInLowerChamber){
    		numPotassiumInLowerChamber = currentNumPotassiumInLowerChamber;
    		concentrationsChanged = true;
    	}
    	
    	if (concentrationsChanged){
    		notifyConcentrationsChanged();
    	}
    }
    
    /**
     * Scan the supplied capture zone for particles of the specified type.
     * 
     * @param zone
     * @param particleType
     * @return
     */
    public CaptureZoneScanResult scanCaptureZoneForFreeParticles(CaptureZone zone, ParticleType particleType){
    	Particle closestFreeParticle = null;
    	double distanceOfClosestParticle = Double.POSITIVE_INFINITY;
    	int totalNumberOfParticles = 0;
    	Point2D captureZoneOrigin = zone.getOriginPoint();
    	
    	for (Particle particle : particles){
    		if ((particle.getType() == particleType) && (particle.isAvailableForCapture()) && (zone.isPointInZone(particle.getPosition()))) {
    			totalNumberOfParticles++;
    			if (closestFreeParticle == null){
    				closestFreeParticle = particle;
    				distanceOfClosestParticle = captureZoneOrigin.distance(closestFreeParticle.getPosition());
    			}
    			else if (captureZoneOrigin.distance(closestFreeParticle.getPosition()) < distanceOfClosestParticle){
    				closestFreeParticle = particle;
    				distanceOfClosestParticle = captureZoneOrigin.distance(closestFreeParticle.getPosition());
    			}
    		}
    	}
    	
    	return new CaptureZoneScanResult(closestFreeParticle, totalNumberOfParticles);
    }
    
	public void addListener(Listener listener){
		listeners.add(Listener.class, listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(Listener.class, listener);
	}
	
	private void notifyChannelAdded(MembraneChannel channel){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.channelAdded(channel);
		}
	}
	
	private void notifyParticleAdded(Particle particle){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.particleAdded(particle);
		}
	}
	
	private void notifyConcentrationGraphVisibilityChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.concentrationGraphVisibilityChanged();
		}
	}
	
	private void notifyConcentrationsChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.concentrationsChanged();
		}
	}
	
    /**
     * Remove all particles (i.e. ions) from the simulation.
     */
    public void removeAllParticles(){
    	// Remove all particles.  This is done by telling each particle to
    	// send out notifications of its removal from the model.  All
    	// listeners, including this class, should remove their references in
    	// response.
    	ArrayList<Particle> particlesCopy = new ArrayList<Particle>(particles);
    	for (Particle particle : particlesCopy){
    		particle.removeFromModel();
    	}
    }
    
    /**
     * Remove all membrane channels from the simulation.
     */
    private void removeAllChannels(){
    	// Remove all membrane channels.  This is done by telling each
    	// channel to send out notifications of its removal from the model.
    	// All listeners, including this class, should remove their references
    	// in response.
    	ArrayList<MembraneChannel> membraneChannelsCopy = new ArrayList<MembraneChannel>(membraneChannels);
    	for (MembraneChannel membraneChannel : membraneChannelsCopy){
    		membraneChannel.removeFromModel();
    	}
    }
    
    /**
     * Add a membrane channel.  It is assumed that the channel is under user
     * control, i.e. the user is dragging from the tool box.
     * 
     * @param membraneChannel
     */
    public void addUserControlledMembraneChannel(final MembraneChannel membraneChannel){
    	assert membraneChannel != null && membraneChannel.isUserControlled();
    	userControlledMembraneChannel = membraneChannel;
    	membraneChannel.addListener( new MembraneChannel.Adapter(){
            @Override
            public void userControlledStateChanged() {
                if (membraneChannel.isUserControlled()){
                    // The user has grabbed this channel.
                    membraneChannels.remove( membraneChannel );
                    userControlledMembraneChannel = membraneChannel;
                }
                else{
                    // The user has released this channel.
                    releaseUserControlledMembraneChannel();
                }
            }
            
            @Override
            public void removed() {
                if (membraneChannels.contains( membraneChannel )){
                    // Take this channel off of the list of membrane channels.
                    membraneChannels.remove(membraneChannel);
                }
                else if (userControlledMembraneChannel == membraneChannel){
                    // This channel was dropped back into the tool box or some
                    // invalid location.
                    userControlledMembraneChannel = null;
                }
            }
    	});
    	notifyChannelAdded(membraneChannel);
    }
    
    /**
     * Release the membrane channel that is currently controlled (i.e. being
     * moved) by the user.  If the user released it within the bounds of the
     * particle chamber and there is a space on the membrane for it, it is
     * moved to the nearest open location on the membrane.  If it is released
     * outside of the particle chamber it is removed from the model. 
     */
    public void releaseUserControlledMembraneChannel(){
    	// Error checking.
    	assert userControlledMembraneChannel != null;
    	
    	if (PARTICLE_CHAMBER_RECT.contains(userControlledMembraneChannel.getCenterLocation())){
    		// The membrane channel was released close enough to the membrane
    		// that an attempt can be made to place it on the membrane. 
    		
    		// Make a list of the open locations on the membrane where the channel
    		// could be placed.
    		ArrayList<Point2D> openLocations = getOpenMembraneLocations();
    		
    		if (openLocations.size() == 0){
    			// If there are no open locations, the channel can't be added.
    			// Remove it from the model.
    			userControlledMembraneChannel.removeFromModel();
    		}
    		else{
    			// Find the closest location.
    			Point2D closestOpenLocation = null;
    			for (Point2D openLocation : openLocations){
    				if (closestOpenLocation == null){
    					closestOpenLocation = openLocation;
    				}
    				else{
    					if (openLocation.distance(userControlledMembraneChannel.getCenterLocation()) < closestOpenLocation.distance(userControlledMembraneChannel.getCenterLocation())){
    						closestOpenLocation = openLocation;
    					}
    				}
    			}
    			
    			// Move the channel to the open location.
    			userControlledMembraneChannel.setCenterLocation(closestOpenLocation);
    			
    			// Put the channel on the list of active channels.
    			membraneChannels.add(userControlledMembraneChannel);
    		}
    	}
    	else{
    		// The channel was released by the user outside of the allowable
    	    // range (possibly just back in the tool box), so it should be
    	    // removed from the model.
			userControlledMembraneChannel.removeFromModel();
    	}
    	
    	// Clear the reference to the membrane channel, since it is no longer
    	// controlled by the user.
    	userControlledMembraneChannel = null;
    }
    
    /**
     * Returns a boolean value indicating whether or not there is space for
     * more membrane channels on the membrane.
     */
    public boolean isMembraneFull(){
        ArrayList<Point2D> openLocations = getOpenMembraneLocations();
        return openLocations.size() == 0;
    }

    /**
     * Compiles a list of locations on the membrane that are open, meaning
     * that the location is not currently occupied by a membrane channel.
     */
    private ArrayList<Point2D> getOpenMembraneLocations(){
        ArrayList<Point2D> openLocations = new ArrayList<Point2D>(allowableChannelLocations);
        for (MembraneChannel membraneChannel : membraneChannels){
            Point2D channelLocation = membraneChannel.getCenterLocation();
            Point2D matchingLocation = null;
            for (Point2D location : openLocations){
                if (location.equals(channelLocation)){
                    // This position is taken.
                    matchingLocation = location;
                }
            }
            if (matchingLocation != null){
                // Remove the matching position from the list.
                openLocations.remove(matchingLocation);
            }
            else{
                System.out.println(getClass().getName() + "Error: Membrane channel not in one of the expected locations.");
                assert false; // Shouldn't happen, debug if it does.
            }
        }
        return openLocations;
    }
    
    public interface Listener extends EventListener {
    	/**
    	 * Notification that a channel was added.
    	 * 
    	 * @param channel - Channel that was added.
    	 */
    	public void channelAdded(MembraneChannel channel);
    	
    	/**
    	 * Notification that a particle was added.
    	 * 
    	 * @param particle - Particle that was added.
    	 */
    	public void particleAdded(Particle particle);
    	
    	/**
    	 * Notification that the concentrations in the sub-chamber have
    	 * changed.
    	 */
    	public void concentrationsChanged();
    	
    	/**
    	 * Notification that the setting for the visibility of the
    	 * concentration graphs has changed.
    	 */
    	public void concentrationGraphVisibilityChanged();
    }
    
    public static class Adapter implements Listener{
		public void channelAdded(MembraneChannel channel) {}
		public void particleAdded(Particle particle) {}
		public void concentrationGraphVisibilityChanged() {}
		public void concentrationsChanged() {}
    }
}
