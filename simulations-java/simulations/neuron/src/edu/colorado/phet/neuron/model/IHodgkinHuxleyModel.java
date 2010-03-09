package edu.colorado.phet.neuron.model;

/**
 * This interface is intended for allowing various instantiations of Hodgkin-
 * Huxley models to be swapped around with minimal code changes.  It may not
 * be needed when a final model is settled upon.
 * 
 * @author John Blanco
 */
public interface IHodgkinHuxleyModel {

	public abstract void reset();

	public abstract double get_n4();

	public abstract double get_m3h();

	//The -1 is to correct for the fact that in the H & H paper, the currents are reversed.	
	public abstract double get_na_current();

	public abstract double get_k_current();

	public abstract double get_l_current();

	public abstract double get_gk();

	public abstract double get_gna();

	public abstract double get_gl();

	public abstract double getElapsedTime();
	
	public abstract double getCm();
	
	public abstract void setCm(double cm);
	
	public abstract void set_gna(double gna);
	
	public abstract void set_gk(double gk);
	
	public abstract void set_gl(double gl);

	/**  
	 * Advances the model by dt (delta time).
	 * 
	 * @param dt - Delta time in seconds.
	 */
	public abstract void stepInTime(double dt);

	/**
	 * Get the membrane potential in volts (Note: not millivolts, which are
	 * used inside the model).
	 * 
	 * @return
	 */
	public abstract double getMembraneVoltage();

	/**
	 * Stimulate the neuron in a way that simulates a depolarization signal
	 * coming to this neuron.  If the neuron is in the correct state, this
	 * will trigger an action potential.
	 */
	public abstract void stimulate();

}