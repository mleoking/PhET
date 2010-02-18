/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.GatedChannel;
import edu.colorado.phet.neuron.model.MembraneChannel;
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
    // Class Data
    //----------------------------------------------------------------------------
	private static final boolean SHOW_CAPTURE_ZONE = false;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	private MembraneChannel membraneChannelModel;
	private ModelViewTransform2D mvt;
	private PPath channel;
	private PPath leftEdgeNode;
	private PPath rightEdgeNode;
	private CaptureZoneNode captureZoneNode = null;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	public MembraneChannelNode(MembraneChannel membraneChannelModel, ModelViewTransform2D mvt){

		this.membraneChannelModel = membraneChannelModel;
		this.mvt = mvt;
		
		// Listen to the channel for changes that may affect the representation.
		membraneChannelModel.addListener(new MembraneChannel.Adapter(){
			public void opennessChanged() {
				updateRepresentation();
			}
		});
		
		// Create the channel representation.
		channel = new PhetPPath(membraneChannelModel.getChannelColor());

		// Create the edge representations.
		double edgeNodeWidth = (membraneChannelModel.getOverallSize().getWidth() - 
				membraneChannelModel.getChannelSize().getWidth()) / 2;
		double edgeNodeHeight = membraneChannelModel.getOverallSize().getHeight();
		Dimension2D transformedEdgeNodeSize = new PDimension(
				Math.abs(mvt.modelToViewDifferentialXDouble(edgeNodeWidth)),
				Math.abs(mvt.modelToViewDifferentialYDouble(edgeNodeHeight)));
		leftEdgeNode = createEdgeNode(transformedEdgeNodeSize, membraneChannelModel.getEdgeColor());
		rightEdgeNode = createEdgeNode(transformedEdgeNodeSize, membraneChannelModel.getEdgeColor());

		// Create the overall composite representation and add the children.
		PNode representation = new PNode();
		representation.addChild(channel);
		representation.addChild(leftEdgeNode);
		representation.addChild(rightEdgeNode);

		// Rotate based on the model element's orientation.
		representation.rotate(-membraneChannelModel.getRotationalAngle() + Math.PI / 2);
		addChild(representation);
		
		// Update the representation.
		updateRepresentation();
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
	
	private void updateRepresentation(){
		
		Dimension2D channelSize = new PDimension(membraneChannelModel.getChannelSize().getWidth() * membraneChannelModel.getOpenness(),
				membraneChannelModel.getChannelSize().getHeight());
		Dimension2D transformedChannelSize = new PDimension(
				Math.abs(mvt.modelToViewDifferentialXDouble(channelSize.getWidth())),
				Math.abs(mvt.modelToViewDifferentialYDouble(channelSize.getHeight())));
		
		// Make the node a bit bigger than the channel so that the edges can
		// be placed over it with no gaps.
		float oversizeFactor = 1.1f;
		
		float width = (float)transformedChannelSize.getWidth() * oversizeFactor;
		float height = (float)transformedChannelSize.getHeight() * oversizeFactor;
		
		GeneralPath path = new GeneralPath();
		path.moveTo(0, 0);
		path.quadTo(width / 2, -height / 8, width, 0);
		path.lineTo(width, -height);
		path.quadTo(width/2, -height * 7 / 8, 0, -height);
		path.closePath();
		
		channel.setPathTo(path);
		channel.setOffset(-channel.getFullBoundsReference().width / 2, channel.getFullBoundsReference().height / 2);

		leftEdgeNode.setOffset(
				-transformedChannelSize.getWidth() / 2 - leftEdgeNode.getFullBoundsReference().width / 2, 0);
		
		rightEdgeNode.setOffset(
				transformedChannelSize.getWidth() / 2 + rightEdgeNode.getFullBoundsReference().width / 2, 0);

		setOffset(mvt.modelToViewDouble(membraneChannelModel.getCenterLocation()));
		
		// If enabled, show/update the capture zone for this channel.
		if (SHOW_CAPTURE_ZONE){
			if (membraneChannelModel instanceof GatedChannel && membraneChannelModel.getOpenness() > 0.1 && captureZoneNode == null){
				// Show the capture node.
				captureZoneNode = new CaptureZoneNode(membraneChannelModel.getCaptureZone(), mvt);
				// Set the offset to the inverse of this node's offset, since
				// the capture zone maintains its own positioning info.
				captureZoneNode.setOffset(-getOffset().getX(), -getOffset().getY());
				addChild(captureZoneNode);
			}
			if (membraneChannelModel instanceof GatedChannel && membraneChannelModel.getOpenness() < 0.1 && captureZoneNode != null){
				// Get rid of the capture zone node.
				removeChild(captureZoneNode);
				captureZoneNode = null;
			}
		}
	}
}
