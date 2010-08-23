/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;


/**
 * A membrane channel openness strategy in which the channel is always closed.
 * 
 * @author John Blanco
 */
public class ChannelAlwaysClosedStrategy extends MembraneChannelOpennessStrategy {
    
    public ChannelAlwaysClosedStrategy(){
        setOpenness( 0 );
    }
}
