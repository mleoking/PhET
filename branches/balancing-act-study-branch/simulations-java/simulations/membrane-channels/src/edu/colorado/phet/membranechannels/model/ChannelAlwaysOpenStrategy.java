// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.model;



/**
 * A membrane channel openness strategy in which the channel is always open.
 * This is intended for usage with leakage channels.
 * 
 * @author John Blanco
 */
public class ChannelAlwaysOpenStrategy extends MembraneChannelOpennessStrategy {

    public ChannelAlwaysOpenStrategy() {
        setOpenness( 1 );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.membranechannels.model.MembraneChannelOpennessStrategy#isDynamic()
     */
    @Override
    protected boolean isDynamic() {
        return false;
    }
}
