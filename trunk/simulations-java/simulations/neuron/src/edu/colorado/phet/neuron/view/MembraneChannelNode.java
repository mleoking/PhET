/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.neuron.model.AbstractMembraneChannel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

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

		PNode representation = new PNode();
		
		Dimension2D channelSize = membraneChannelModel.getChannelSize();
		Rectangle2D transformedChannelRect = new Rectangle2D.Double(
				-mvt.modelToViewDifferentialXDouble(channelSize.getWidth()) / 2,
				mvt.modelToViewDifferentialYDouble(channelSize.getHeight()) / 2,
				mvt.modelToViewDifferentialXDouble(channelSize.getWidth()),
				-mvt.modelToViewDifferentialYDouble(channelSize.getHeight()));
		PPath channel = new PPath(transformedChannelRect);
		channel.setPaint(membraneChannelModel.getChannelColor());
		representation.addChild(channel);
		
		double edgeNodeWidth = (membraneChannelModel.getOverallSize().getWidth() - 
				membraneChannelModel.getChannelSize().getWidth()) / 2;
		double edgeNodeHeight = membraneChannelModel.getOverallSize().getHeight();
		
		Dimension2D transformedEdgeNodeSize = new PDimension(mvt.modelToViewDifferentialXDouble(edgeNodeWidth),
				-mvt.modelToViewDifferentialYDouble(edgeNodeHeight));
				
		PNode leftEdgeNode = createEdgeNode(transformedEdgeNodeSize, membraneChannelModel.getEdgeColor());
		leftEdgeNode.setOffset(channel.getFullBoundsReference().getMinX() - leftEdgeNode.getFullBoundsReference().width,
				leftEdgeNode.getFullBoundsReference().height / 2);
		representation.addChild(leftEdgeNode);
		
		PNode rightEdgeNode = createEdgeNode(transformedEdgeNodeSize, membraneChannelModel.getEdgeColor());
		rightEdgeNode.setOffset(channel.getFullBoundsReference().getMaxX(),
				rightEdgeNode.getFullBoundsReference().height / 2);
		representation.addChild(rightEdgeNode);

		representation.rotate(-membraneChannelModel.getRotationalAngle() + Math.PI / 2);
		addChild(representation);
		
//		channel.rotate(-membraneChannelModel.getRotationalAngle() + Math.PI / 2);
//		System.out.println("Angle = " + membraneChannelModel.getRotationalAngle() * 180 / Math.PI);
		
		// TODO: Positioning node, uncomment when needed, remove eventually.
//		PPath positioningNode = new PhetPPath(new Ellipse2D.Double(-10, -10, 20, 20), Color.PINK);
//		addChild(positioningNode);
		
		setOffset(mvt.modelToViewDouble(membraneChannelModel.getCenterLocation()));
	}
	
	
	private PPath createEdgeNode(Dimension2D size, Color color){
		
		GeneralPath path = new GeneralPath();
		
		float width = (float)size.getWidth();
		float height = (float)size.getHeight();
		path.moveTo(0, -height / 4);
		path.quadTo(width / 2, height / 4, width, -height / 4);
		path.lineTo(width, -height * 3 / 4);
		path.quadTo(width/2, -height * 5 / 4, 0, -height * 3 / 4);
		path.closePath();
		
		PPath edgeNode = new PPath(path);
		edgeNode.setPaint(color);
		
		return edgeNode;
	}
}
