/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

/**
 * This class is a delay buffer that allows information to be put into it
 * and then extracted based on the amount of time in the past that a value
 * is needed.
 * 
 * NOTE: If this turns out to be useful for other applications, it should be
 * made to handle generics.
 * 
 * @author John Blanco
 */
public class DelayBuffer {

	private DelayElement[] delayElements;
	private double value; // TODO: Temp, delete.  Soon.
	
	public void addValue(double value, double deltaTime){
		// Stubbed for now.
		this.value = value;
	}
	
	public double getDelayedValue(double delayAmount){
		// Stubbed for now.
		return value;
	}
	
	public void clear(){
		// Stubbed for now.
	}
	
	private class DelayElement{
		
		private double value;
		private double deltaTime;

		public DelayElement(double value, double deltaTime) {
			super();
			this.value = value;
			this.deltaTime = deltaTime;
		}

		protected double getValue() {
			return value;
		}

		protected void setValue(double value) {
			this.value = value;
		}

		protected double getDeltaTime() {
			return deltaTime;
		}

		protected void setDeltaTime(double deltaTime) {
			this.deltaTime = deltaTime;
		}
	}
}
