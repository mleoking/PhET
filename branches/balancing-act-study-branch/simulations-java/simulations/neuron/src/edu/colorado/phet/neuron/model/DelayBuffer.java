// Copyright 2002-2011, University of Colorado

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
	
	// This value is used to tell if two numbers are different.  It was needed
	// due to some floating point resolution problems that were occurring.
	private static final double DIFFERENCE_RESOLUTION = 1E-15;
	
	private DelayElement[] delayElements;
	private int numEntries;
	private boolean filling;
	private boolean allDeltaTimesEqual = true;
	private double previousDeltaTime = -1;
	private int countAtThisDeltaTime = 0;
	
	// Head and tail pointers for FIFO-type behavior.  FIFO management is
	// done explicitly rather than using the Queue class in order to do some
	// optimizations for performance.
	int head;
	int tail;
	
	public DelayBuffer(double maxDelay, double minTimeStep){
		numEntries = (int)Math.ceil(maxDelay / minTimeStep);
		
		// Allocate the memory that will be used.
		delayElements = new DelayElement[numEntries];
		for (int i = 0; i < numEntries; i++){
			delayElements[i] = new DelayElement();
		}
		
		// Set the initial conditions.
		clear();
	}
	
	public void addValue(double value, double deltaTime){
		delayElements[head].setValueAndTime(value, deltaTime);
		head = (head + 1) % numEntries;
		if (head == tail){
			// The addition of this element has overwritten what was the tail
			// of the queue, so it must be advanced.
			tail = (tail + 1) % numEntries;
			
			// Once full, it will stay full, since there is no reason to
			// remove values from the queue.
			filling = false;
		}
		
		// Update the flag that determines if all the delta time values
		// currently stored are the same.
		if (previousDeltaTime == -1){
			// First time through, just store the time.
			previousDeltaTime = deltaTime;
			countAtThisDeltaTime = 1;
		}
		else{
			if (Math.abs(deltaTime - previousDeltaTime) > DIFFERENCE_RESOLUTION){
				// The time increment has changed, so we know that there are
				// different time values in the queue.
				allDeltaTimesEqual = false;
				countAtThisDeltaTime = 1;
			}
			else{
				if (!allDeltaTimesEqual){
					// The new value is equal to the previous value, but the
					// flag says that not all values were the same.  Does the
					// addition of this value make them all equal?
					countAtThisDeltaTime++;
					if (countAtThisDeltaTime >= numEntries){
						// All delta times should now be equal, so set the
						// flag accordingly.
						allDeltaTimesEqual = true;
					}
				}
			}
			previousDeltaTime = deltaTime;
		}
	}
	
	public double getDelayedValue(double delayAmount){
		double delayedValue = 0;
		if (previousDeltaTime <= 0){
			// No data has been added yet, return 0.
			delayedValue = 0;
		}
		else if (allDeltaTimesEqual){
			// All times in the buffer are equal, so we should be able to
			// simply index to the appropriate location.  The offset must be
			// at least 1, since this buffer doesn't hold a non-delayed value.
			int offset = (int)Math.max(Math.round(delayAmount / previousDeltaTime), 1);
			if ((filling && offset > head) || offset > numEntries){
				// The user is asking for data that we don't have yet, so
				// give them the oldest data available.
				delayedValue = delayElements[tail].getValue(); 
			}
			else{
				int index = head - offset;
				if (index < 0){
					// Handle wraparound.
					index = numEntries + index;
				}
				delayedValue = delayElements[index].getValue();
			}
		}
		else{
			// There is variation in the delta time values in the buffer, so
			// we need to go through them, add the delays, and find the
			// closest data.
			boolean delayReached = false;
			int index = head > 0 ? head - 1 : numEntries - 1;
			double accumulatedDelay = 0;
			while (!delayReached){
				accumulatedDelay += delayElements[index].getDeltaTime();
				if (accumulatedDelay >= delayAmount){
					// We've found the data.  Note that it may not be the
					// exact time requested - we're assuming it is close
					// enough.  Might need to add interpolation some day if
					// more accuracy is needed.
					delayReached = true;
				}
				else if (index == tail){
					// We've gone through all the data and there isn't enough
					// to obtain the requested delay amount, so return the
					// oldest that is available.
					delayReached = true;
				}
				else{
					// Keep going through the buffer.
					index = index - 1 > 0 ? index - 1 : numEntries - 1;
				}
			}
			delayedValue = delayElements[index].getValue();
		}
		
		return delayedValue;
	}
	
	public void clear(){
		head = 0;
		tail = 0;
		previousDeltaTime = -1;
		filling = true;
	}
	
	private class DelayElement{
		
		private double value;
		private double deltaTime;

		public DelayElement(double value, double deltaTime) {
			super();
			this.value = value;
			this.deltaTime = deltaTime;
		}
		
		public DelayElement(){
			this(0, 0);
		}

		protected double getValue() {
			return value;
		}

		protected void setValueAndTime(double value, double deltaTime){
			this.value = value;
			this.deltaTime = deltaTime;
		}

		protected double getDeltaTime() {
			return deltaTime;
		}
	}
}
