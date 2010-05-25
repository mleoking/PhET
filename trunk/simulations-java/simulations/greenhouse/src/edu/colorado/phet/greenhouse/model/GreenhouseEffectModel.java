/* Copyright 2009, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.neuron.model.Particle;
import edu.colorado.phet.neuron.model.AxonModel.Listener;

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

	private static final double PHOTON_EXISTENCE_TIME = 100;
	
	private ArrayList<Photon> photons = new ArrayList<Photon>();
	private double photonExistenceCounter = PHOTON_EXISTENCE_TIME;
	private Photon photon = null;
    private EventListenerList listeners = new EventListenerList();

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
			photon.setLocation(0, 0);
			photon.setVelocity(1f, 0);
			notifyPhotonAdded(photon);
		}
		else{
			photonExistenceCounter -= dt;
			if (photonExistenceCounter >= 0){
				photon.stepInTime(dt);
			}
			else{
				photon = null;
				photonExistenceCounter = PHOTON_EXISTENCE_TIME;
				// TODO - Absorb the photon.
			}
		}
		
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
	
	public static class Adapter implements Listener{
		public void photonAdded(Photon photon) {};
	}
	
	public interface Listener extends EventListener {
		public void photonAdded(Photon photon);
	}
}
