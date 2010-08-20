/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;


/**
 * A membrane channel openness strategy in which the channel is always open.
 * This is intended for usage with leakage channels.
 * 
 * @author John Blanco
 */
public class ChannelAlwaysOpenStrategy extends MembraneChannelOpennessStrategy {

    /* (non-Javadoc)
     * @see edu.colorado.phet.membranediffusion.model.MembraneChannelOpennessStrategy#getOpenness()
     */
    @Override
    public double getOpenness() {
        return 1;
    }
}
