/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	public MembraneChannelNode(AbstractMembraneChannel membraneChannelModel, ModelViewTransform2D mvt){

		PNode representation = new PNode();
		
		Dimension2D channelSize = membraneChannelModel.getChannelSize();
		Dimension2D transformedChannelSize = new PDimension(
				mvt.modelToViewDifferentialXDouble(channelSize.getWidth()),
				-mvt.modelToViewDifferentialYDouble(channelSize.getHeight()));
		PPath channel = createChannelNode(transformedChannelSize, membraneChannelModel.getChannelColor());
		channel.setOffset(-channel.getFullBoundsReference().width / 2, channel.getFullBoundsReference().height / 2);
		representation.addChild(channel);

		double edgeNodeWidth = (membraneChannelModel.getOverallSize().getWidth() - 
				membraneChannelModel.getChannelSize().getWidth()) / 2;
		double edgeNodeHeight = membraneChannelModel.getOverallSize().getHeight();
		
		Dimension2D transformedEdgeNodeSize = new PDimension(mvt.modelToViewDifferentialXDouble(edgeNodeWidth),
				-mvt.modelToViewDifferentialYDouble(edgeNodeHeight));
				
		PNode leftEdgeNode = createEdgeNode(transformedEdgeNodeSize, membraneChannelModel.getEdgeColor());
		leftEdgeNode.setOffset(
				-transformedChannelSize.getWidth() / 2 - leftEdgeNode.getFullBoundsReference().width / 2, 0);
		representation.addChild(leftEdgeNode);
		
		PNode rightEdgeNode = createEdgeNode(transformedEdgeNodeSize, membraneChannelModel.getEdgeColor());
		rightEdgeNode.setOffset(
				transformedChannelSize.getWidth() / 2 + rightEdgeNode.getFullBoundsReference().width / 2, 0);
		representation.addChild(rightEdgeNode);

		representation.rotate(-membraneChannelModel.getRotationalAngle() + Math.PI / 2);
		addChild(representation);
		
		// TODO: Positioning node, uncomment when needed, remove eventually.
//		PPath positioningNode = new PhetPPath(new Ellipse2D.Double(-10, -10, 20, 20), Color.PINK);
//		addChild(positioningNode);
		
		setOffset(mvt.modelToViewDouble(membraneChannelModel.getCenterLocation()));
	}
	
	
	private PPath createEdgeNode(Dimension2D size, Color color){
		
		GeneralPath path = new GeneralPath();
		
		float width = (float)size.getWidth();
		float height = (float)size.getHeight();

		path.moveTo(-width / 2, height / 4);
		path.curveTo(-width / 2, height / 2, width / 2, height / 2, width / 2, height / 4);
		path.lineTo(width / 2, -height / 4);
		path.curveTo(width / 2, -height / 2, -width / 2, -height / 2, -width / 2, -height / 4);
		path.closePath();

		PPath edgeNode = new PPath(path);
		edgeNode.setPaint(color);
		edgeNode.setStrokePaint(ColorUtils.darkerColor(color, 0.3));
		
		return edgeNode;
	}
	
	private PPath createChannelNode(Dimension2D size, Color color){
		
		// Make the node a bit bigger than the channel so that the edges can
		// be placed over it with no gaps.
		float oversizeFactor = 1.1f;
		
		float width = (float)size.getWidth() * oversizeFactor;
		float height = (float)size.getHeight() * oversizeFactor;
		
		GeneralPath path = new GeneralPath();
		path.moveTo(0, 0);
		path.quadTo(width / 2, -height / 8, width, 0);
		path.lineTo(width, -height);
		path.quadTo(width/2, -height * 7 / 8, 0, -height);
		path.closePath();
		
		return( new PhetPPath(path, color, (Stroke)null, color) );
	}
}
