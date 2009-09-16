/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.neuron.model.AbstractMembraneChannel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Node that represents membrane channels in the view.
 * 
 * @author John Blanco
 */
public class MembraneChannelNode extends PNode{
	
    private PNode parentNode; // TODO: Use this to implement removal of nodes.
	
	public MembraneChannelNode(AbstractMembraneChannel membraneChannelModel, PNode parentNode, ModelViewTransform2D mvt){

		this.parentNode = parentNode;
		
		PPath channel = new PPath(mvt.createTransformedShape(membraneChannelModel.getChannelRect()));
		channel.setPaint(membraneChannelModel.getChannelColor());
		addChild(channel);
	}
	
	
	private PPath createPillNode(Rectangle2D definingRect){
		
		return null;
		
	}
}
