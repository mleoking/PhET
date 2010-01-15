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
	private static final double POTASSIUM_ELECTROCHEMICAL_GRADIENT = -0.080; // In volts.
	private static final double LEAK_ELECTROCHEMICAL_GRADIENT = -0.070; // In volts.
	private static final double INITIAL_MEMBRANE_VOLTAGE_POTENTIAL = -0.070;  // In volts.
	private static final double INITIAL_SODIUM_CHANNEL_RESISTANCE = 100E3;
	private static final double INITIAL_POTASSIUM_CHANNEL_RESISTANCE = Double.POSITIVE_INFINITY;
	private static final double INITIAL_LEAK_CHANNEL_RESISTANCE = 100E3; // Ohms

	private double gatedSodiumChannelResistence = INITIAL_SODIUM_CHANNEL_RESISTANCE;
	private double gatedPotassiumChannelResistence = INITIAL_POTASSIUM_CHANNEL_RESISTANCE;
	private double leakChannelResistence = INITIAL_LEAK_CHANNEL_RESISTANCE;
	
	private double membraneVoltagePotential = INITIAL_MEMBRANE_VOLTAGE_POTENTIAL;  // In volts.
	
	/**
	 * Step the model in time.
	 * 
	 * @param dt - amount of time for this step, in milliseconds.
	 */
	public void stepInTime(double dt){
		
		// Make any changes to the states of the gates based 
		
		// Calculate the current flowing into the capacitor based on the value
		// of the voltage at this point in time.
		double totalCurrent = 0;
		
		// TODO: Are the 'if' statements needed, or can we just count on dividing by infinity
		// to yield a zero?
		
		// Add the contribution from the leak channels.
		if (leakChannelResistence != Double.POSITIVE_INFINITY){
			totalCurrent += (membraneVoltagePotential - LEAK_ELECTROCHEMICAL_GRADIENT) / leakChannelResistence;
		}
		
		// Add the contribution from the sodium channels.
		if (gatedSodiumChannelResistence != Double.POSITIVE_INFINITY){
			totalCurrent += (membraneVoltagePotential - SODIUM_ELECTROCHEMICAL_GRADIENT) / gatedSodiumChannelResistence; 
		}
		
		// Add the contribution from the potassium channels.
		if (gatedPotassiumChannelResistence != Double.POSITIVE_INFINITY){
			totalCurrent += (membraneVoltagePotential - POTASSIUM_ELECTROCHEMICAL_GRADIENT) / gatedPotassiumChannelResistence; 
		}
		
		// Now update the voltage.
		membraneVoltagePotential += totalCurrent / MEMBRANE_CAPACITANCE;
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
