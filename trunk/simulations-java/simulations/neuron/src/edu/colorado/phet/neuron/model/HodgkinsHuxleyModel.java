/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

/**
 * This class is an iterative implementation of the Hodgkins Huxley model,
 * which is a circuit model the describes how action potentials are initiated
 * and propagated in neurons.  More information on the model itself can be
 * found on the web.
 * 
 * Note: As with the convention in biology, all potentials are defined from
 * the outside to the inside.  In other words, if you were using a voltmeter
 * to measure these potentials, the black lead would be on the outside of the
 * cell membrane and the red lead would be on the inside.
 * 
 * @author John Blanco
 */
public class HodgkinsHuxleyModel {
	
	private static final double MEMBRANE_CAPACITANCE = 0.00002;  // In farads.
	private static final double SODIUM_ELECTROCHEMICAL_GRADIENT = 0.060; // In volts.
	private static final double POTASSIUM_ELECTROCHEMICAL_GRADIENT = -0.090; // In volts.
	private static final double LEAK_ELECTROCHEMICAL_GRADIENT = -0.070; // In volts.
	private static final double INITIAL_MEMBRANE_VOLTAGE_POTENTIAL = -0.070;  // In volts.
	private static final double INITIAL_SODIUM_CHANNEL_RESISTANCE = Double.POSITIVE_INFINITY;
	private static final double INITIAL_POTASSIUM_CHANNEL_RESISTANCE = Double.POSITIVE_INFINITY;
	private static final double INITIAL_LEAK_CHANNEL_RESISTANCE = 5E6; // Ohms
	
	private static final double SODIUM_CHANNEL_CLOSING_THRESHOLD = 0.030;
	private static final double POTASSIUM_CHANNEL_CLOSING_THRESHOLD = -0.085;

	private double gatedSodiumChannelResistence = INITIAL_SODIUM_CHANNEL_RESISTANCE;
	private double gatedPotassiumChannelResistence = INITIAL_POTASSIUM_CHANNEL_RESISTANCE;
	private double leakChannelResistence = INITIAL_LEAK_CHANNEL_RESISTANCE;
	
	private double membraneVoltagePotential = INITIAL_MEMBRANE_VOLTAGE_POTENTIAL;  // In volts.
	
	/**
	 * Step the model in time.
	 * 
	 * @param dt - amount of time for this step, in seconds.
	 */
	public void stepInTime(double dt){
		
		// Make any changes to the states of the gates based on state.
		
		
		// Calculate the current flowing into the capacitor based on the value
		// of the voltage at this point in time.
		double totalCurrent = 0;
		
		// TODO: Are the 'if' statements needed, or can we just count on dividing by infinity
		// to yield a zero?
		
		// Add the contribution from the leak channels.
		double leakChannelCurrent = 0;
		if (leakChannelResistence != Double.POSITIVE_INFINITY){
			leakChannelCurrent = (LEAK_ELECTROCHEMICAL_GRADIENT - membraneVoltagePotential) / leakChannelResistence;
			System.out.println("-> leak channel current = " + leakChannelCurrent);
		}
		totalCurrent += leakChannelCurrent;
		
		// Add the contribution from the sodium channels.
		double sodiumChannelCurrent = 0;
		if (gatedSodiumChannelResistence != Double.POSITIVE_INFINITY){
			sodiumChannelCurrent = (SODIUM_ELECTROCHEMICAL_GRADIENT - membraneVoltagePotential) / gatedSodiumChannelResistence;
			System.out.println("--> sodium channel current = " + sodiumChannelCurrent);
			if (membraneVoltagePotential >= SODIUM_CHANNEL_CLOSING_THRESHOLD){
				// Time to close the sodium channel...
				gatedSodiumChannelResistence = Double.POSITIVE_INFINITY;
				// ...and open the potassium channel.
				gatedPotassiumChannelResistence = 500E3;
			}
		}
		totalCurrent += sodiumChannelCurrent; 
		
		// Add the contribution from the potassium channels.
		double postassiumChannelCurrent = 0;
		if (gatedPotassiumChannelResistence != Double.POSITIVE_INFINITY){
			postassiumChannelCurrent = (POTASSIUM_ELECTROCHEMICAL_GRADIENT - membraneVoltagePotential) / gatedPotassiumChannelResistence;
			System.out.println("---> potassium channel current = " + postassiumChannelCurrent);
			if (membraneVoltagePotential <= POTASSIUM_CHANNEL_CLOSING_THRESHOLD){
				// Time to close the potassium channel.
				gatedPotassiumChannelResistence = Double.POSITIVE_INFINITY;
			}
		}
		totalCurrent += postassiumChannelCurrent;
		
		// Now update the voltage.
		membraneVoltagePotential += totalCurrent / MEMBRANE_CAPACITANCE;
		System.out.println("------> membrane voltage = " + membraneVoltagePotential);
	}
	
	/**
	 * Initiate an action potential.
	 *  
	 * @return
	 */
	public void stimulate(){
		// TODO: I'm not sure of the best way to do this.  The spec says that
		// a stimulus arrives and raises the potential to -55mv (from the
		// usual level of -70mv).  So should I just set the potential
		// immediately to -55mV?  What if it isn't at -70mV?  Or should the
		// change somehow be more gradual?
		membraneVoltagePotential = -0.055;
		gatedSodiumChannelResistence = 500E3;
	}
	
	public double getMembraneVoltagePotential(){
		return membraneVoltagePotential;
	}
	
	public double getGatedSodiumChannelResistence() {
		return gatedSodiumChannelResistence;
	}
	
	public double getGatedPotassiumChannelResistence() {
		return gatedPotassiumChannelResistence;
	}
	
	public double getLeakChannelResistence() {
		return leakChannelResistence;
	}
	
	public static void main(String[] args) {
		System.out.println("Begin Hodgkins-Huxley model test.");
		HodgkinsHuxleyModel model = new HodgkinsHuxleyModel();
		for (int i = 0; i < 100; i++){
			model.stepInTime(0.030);
			System.out.println("Membrane voltage = " + model.getMembraneVoltagePotential());
		}
	}
}
