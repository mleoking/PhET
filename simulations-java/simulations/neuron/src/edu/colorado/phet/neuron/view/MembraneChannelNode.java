/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import edu.colorado.phet.neuron.model.AbstractMembraneChannel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Node that represents membrane channels in the view.
 * 
 * @author John Blanco
 */
public class MembraneChannelNode extends PNode{

	private PPath channel;
	private PPath leftSide;
	private PPath rightSide;
	
	public MembraneChannelNode(AbstractMembraneChannel membraneChannelModel){
		
	}
}
