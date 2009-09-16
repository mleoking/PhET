/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.AbstractMembraneChannel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Node that represents a membrane channel in the view.
 * 
 * @author John Blanco
 */
public class MembraneChannelNode extends PNode{
	
    private PNode parentNode; // TODO: Use this to implement removal of nodes.

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	public MembraneChannelNode(AbstractMembraneChannel membraneChannelModel, PNode parentNode, ModelViewTransform2D mvt){

		this.parentNode = parentNode;

		Dimension2D channelSize = membraneChannelModel.getChannelSize();
		Rectangle2D transformedChannelRect = new Rectangle2D.Double(
				-mvt.modelToViewDifferentialXDouble(channelSize.getWidth()) / 2,
				mvt.modelToViewDifferentialYDouble(channelSize.getHeight()) / 2,
				mvt.modelToViewDifferentialXDouble(channelSize.getWidth()),
				-mvt.modelToViewDifferentialYDouble(channelSize.getHeight()));
		PPath channel = new PPath(transformedChannelRect);
		channel.setPaint(membraneChannelModel.getChannelColor());
		channel.rotate(-membraneChannelModel.getRotationalAngle() + Math.PI / 2);
		System.out.println("Angle = " + membraneChannelModel.getRotationalAngle() * 180 / Math.PI);
		addChild(channel);
		
		// Move the channel representation so that 
		PPath positioningNode = new PhetPPath(new Ellipse2D.Double(-10, -10, 20, 20), Color.PINK);
		addChild(positioningNode);
		
		setOffset(mvt.modelToViewDouble(membraneChannelModel.getCenterLocation()));
	}
	
	
	private PPath createPillNode(Rectangle2D definingRect){
		
		return null;
		
	}
}
