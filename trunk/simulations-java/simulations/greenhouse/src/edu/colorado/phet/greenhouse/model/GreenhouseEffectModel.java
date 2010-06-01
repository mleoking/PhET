/* Copyright 2009, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Random;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.greenhouse.GreenhouseConfig;

/**
 * Main model for the Greenhouse Effect.  This class is the central point
 * that the view and control use when interacting with the model, and as such
 * it contains and controls all other model components, such as photons,
 * clouds, etc.
 * 
 * @author Ron LeMaster, John Blanco
 *
 */
public class GreenhouseEffectModel {

	private static final int MAX_NUM_CLOUDS = 3;
	
	private static final double PHOTON_EXISTENCE_TIME = 400;
	private static final Random RAND = new Random();
	
	private ArrayList<Photon> photons = new ArrayList<Photon>();
	private ArrayList<Cloud> clouds = new ArrayList<Cloud>();
	private EventListenerList listeners = new EventListenerList();
	
	// TODO: Temp for debug.
	private double photonExistenceCounter = PHOTON_EXISTENCE_TIME;
	private Photon photon = null;

	public GreenhouseEffectModel(GreenhouseClock clock){
		clock.addClockListener(new ClockAdapter(){
		    public void clockTicked( ClockEvent clockEvent ) {
		    	stepInTime(clockEvent.getSimulationTimeChange());
		    }
		});
	}
	
	private void stepInTime(double dt){
		
		if (photon == null){
			photon = new Photon(GreenhouseConfig.irWavelength, null);
			double xPos = (RAND.nextDouble() - 0.5) * 200;
			photon.setLocation(xPos, -70);
			photon.setVelocity(0, 0.2f);
			notifyPhotonAdded(photon);
		}
		else{
			photonExistenceCounter -= dt;
			if (photonExistenceCounter >= 0){
				photon.stepInTime(dt);
			}
			else{
				photonExistenceCounter = PHOTON_EXISTENCE_TIME;
				notifyPhotonRemoved(photon);
				photon = null;
				// TODO - Absorb the photon.
			}
		}
	}
	
	public int addCloud(){
		if (clouds.size() <= MAX_NUM_CLOUDS){
			// Add a cloud.
			Cloud cloud = createCloud(clouds.size());
			clouds.add(cloud);
			notifyCloudAdded(cloud);
		}
		else{
			System.out.println(getClass().getName() + " - Warning: Attempt to add too many clouds, ignoring.");
		}
		
		return clouds.size();
	}
	
	public int removeCloud(){
		
		if (clouds.size() > 0){
			Cloud cloudToRemove = clouds.get(clouds.size() - 1);
			clouds.remove(clouds.remove(clouds.size() - 1));
			notifyCloudRemoved(cloudToRemove);
		}
		else{
			System.out.println(getClass().getName() + " - Warning: Attempt to remove non-existent cloud, ignoring.");
		}
		
		return clouds.size();
	}
	
	public static int getMaxNumClouds(){
		return MAX_NUM_CLOUDS;
	}
	
	public int getNumClouds(){
		return clouds.size();
	}
	
	/**
	 * Create clouds based on the number of clouds present.  This ensures
	 * consistent behavior when clouds are added.
	 * 
	 * @return
	 */
	private Cloud createCloud(int index){
		assert index >= 0 && index < MAX_NUM_CLOUDS - 1;
		Ellipse2D.Double cloudBounds = new Ellipse2D.Double();
		switch (index){
		case 0:
			cloudBounds.setFrame(35, -12, 80, 10);
			break;
		case 1:
			cloudBounds.setFrame(-95, -20, 80, 10);
			break;
		case 2:
			cloudBounds.setFrame(-10, 10, 50, 8);
			break;
		default:
			System.out.println(getClass().getName() + " - Error: Cloud creation index out of range.");
			cloudBounds.setFrame(-200, -200, 100, 100);
			break;
		}
		
		return new Cloud(cloudBounds);
		
	}
	
	public void addListener(Listener listener){
		listeners.add(Listener.class, listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(Listener.class, listener);
	}
	
	private void notifyPhotonAdded(Photon photon){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.photonAdded(photon);
		}
	}
	
	private void notifyPhotonRemoved(Photon photon){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.photonRemoved(photon);
		}
	}
	
	private void notifyCloudAdded(Cloud cloud){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.cloudAdded(cloud);
		}
	}
	
	private void notifyCloudRemoved(Cloud cloud){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.cloudRemoved(cloud);
		}
	}
	
	public static class Adapter implements Listener{
		public void photonAdded(Photon photon) {};
		public void photonRemoved(Photon photon) {}
		public void cloudAdded(Cloud cloud) {}
		public void cloudRemoved(Cloud cloud) {};
	}
	
	public interface Listener extends EventListener {
		public void photonAdded(Photon photon);
		public void photonRemoved(Photon photon);
		public void cloudAdded(Cloud cloud);
		public void cloudRemoved(Cloud cloud);
	}
}
