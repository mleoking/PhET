package edu.colorado.phet.neuron.model;

/**
 * This class is an alternative to the standard Hodgkin-Huxley model for
 * calculating the conductances of the sodium and potassium channels on a
 * neuron membrane as a function of time.
 *  
 * @author John Blanco, Noah Podolefsky (provided the equations)
 *
 */
public class AlternativeConductanceModel {
	
	private static final double SODUM_AMPLITUDE = 1;
	private static final double SODIUM_WIDTH = 0.3;
	private static final double SODIUM_OFFSET = 0.5;
	private static final double POTASSIUM_AMPLITUDE = 0.21;
	private static final double POTASSIUM_WIDTH = 1.0;
	private static final double POTASSIUM_OFFSET = 3.0;

	private double timeSinceLastActionPotentialInitiation = Double.POSITIVE_INFINITY;
	
	public void stepInTime(double dt){
		if (timeSinceLastActionPotentialInitiation < Double.POSITIVE_INFINITY){
			timeSinceLastActionPotentialInitiation += dt;
		}
	}
	
	public double getSodiumConductance(){
		return SODUM_AMPLITUDE * Math.exp( -1 / SODIUM_WIDTH * Math.pow(timeSinceLastActionPotentialInitiation * 1000 - SODIUM_OFFSET, 2));
	}
	
	public double getPotassiumConductance(){
		return POTASSIUM_AMPLITUDE * Math.exp( -1 / POTASSIUM_WIDTH * Math.pow(timeSinceLastActionPotentialInitiation * 1000 - POTASSIUM_OFFSET, 2));
	}
	
	public void initiateActionPotential(){
		timeSinceLastActionPotentialInitiation = 0;
	}
	
	public void reset(){
		timeSinceLastActionPotentialInitiation = Double.POSITIVE_INFINITY;
	}
}
