// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.model;



/**
 * A membrane channel openness strategy in which the channel is always closed.
 * 
 * @author John Blanco
 */
public class ChannelAlwaysClosedStrategy extends MembraneChannelOpennessStrategy {
    
    public ChannelAlwaysClosedStrategy(){
        setOpenness( 0 );
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.membranechannels.model.MembraneChannelOpennessStrategy#isDynamic()
     */
    @Override
    protected boolean isDynamic() {
        return false;
    }
}
