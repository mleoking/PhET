/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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
	private PNode channelLayer;
	private PNode edgeLayer;
	private PPath channel;
	private PPath leftEdgeNode;
	private PPath rightEdgeNode;
	private CaptureZoneNode captureZoneNode;
	private PNode inactivationGateBallNode;
	private PPath inactivationGateString;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	/**
	 * Constructor.  Note that the parent nodes are passed in so that the
	 * layering appearance can be better controlled.
	 */
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
		
		// Create the layers for the channel the edges.  This makes offsets
		// and rotations easier.
		channelLayer = new PNode();
		addChild(channelLayer);
		channelLayer.addChild(channel);
		edgeLayer = new PNode();
		addChild(edgeLayer);
		edgeLayer.addChild(leftEdgeNode);
		edgeLayer.addChild(rightEdgeNode);
		
		if (membraneChannelModel.getHasInactivationGate()){
			
			// Add the ball and string that make up the inactivation gate.
			
			inactivationGateString = new PhetPPath(new BasicStroke(2f), Color.BLACK);
			channelLayer.addChild(inactivationGateString);

			double ballDiameter = mvt.modelToViewDifferentialXDouble(membraneChannelModel.getChannelSize().getWidth());
			Shape inactivationBallShape = 
				new Ellipse2D.Double(-ballDiameter / 2, -ballDiameter / 2, ballDiameter, ballDiameter);
			inactivationGateBallNode = new PhetPPath(inactivationBallShape, membraneChannelModel.getEdgeColor(),
					new BasicStroke(1f), ColorUtils.darkerColor(membraneChannelModel.getEdgeColor(), 0.3));
			channelLayer.addChild(inactivationGateBallNode);
			
		}
		
		// Add the capture zone, which is only used for debug.
		if (SHOW_CAPTURE_ZONE){
			captureZoneNode = new CaptureZoneNode(membraneChannelModel.getCaptureZone(), mvt);
			captureZoneNode.setVisible(false);
			channelLayer.addChild(captureZoneNode);
		}

		// Update the representation.
		updateRepresentation();
	}
	
	/**
	 * Add this node to the two specified parent nodes.  This is done in order
	 * to achieve a better layering effect that allows the particles to look
	 * more like they are moving through the channel.  It is not absolutely
	 * necessary to use this method for this node - it can be added to the
	 * canvas like any other PNode, it just won't have the layering.
	 * 
	 * @param channelLayer
	 * @param edgeLayer
	 */
	public void addToCanvas(PNode channelLayer, PNode edgeLayer){
		channelLayer.addChild(this.channelLayer);
		edgeLayer.addChild(this.edgeLayer);
		edgeLayer.addChild(this.edgeLayer);
	}
	
	public void removeFromCanvas(PNode channelLayer, PNode edgeLayer){
		channelLayer.removeChild(channel);
		edgeLayer.removeChild(rightEdgeNode);
		edgeLayer.removeChild(leftEdgeNode);
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
		path.quadTo(width * 0.6f, -height / 8.0f, width * 1.2f, 0);
		path.lineTo(width * 1.2f, -height);
		path.quadTo(width * 0.6f, -height * 7 / 8, 0, -height);
		path.closePath();
		
		channel.setPathTo(path);
		channel.setOffset(-channel.getFullBoundsReference().width / 2, channel.getFullBoundsReference().height / 2);

		leftEdgeNode.setOffset(
				-transformedChannelSize.getWidth() / 2 - leftEdgeNode.getFullBoundsReference().width / 2, 0);
		
		rightEdgeNode.setOffset(
				transformedChannelSize.getWidth() / 2 + rightEdgeNode.getFullBoundsReference().width / 2, 0);

		channelLayer.setOffset(mvt.modelToViewDouble(membraneChannelModel.getCenterLocation()));
		edgeLayer.setOffset(mvt.modelToViewDouble(membraneChannelModel.getCenterLocation()));
		
		if (membraneChannelModel.getHasInactivationGate()){
			// Position the ball portion of the inactivation gate.
			inactivationGateBallNode.setOffset(
				leftEdgeNode.getFullBoundsReference().getMaxX() - inactivationGateBallNode.getFullBoundsReference().width / 2,
				transformedChannelSize.getHeight());

			// Redraw the "string" (actually a strand of protein in real life)
			// that connects the ball to the gate.
			Point2D channelConnectionPoint = new Point2D.Double(leftEdgeNode.getFullBoundsReference().getCenterX(),
					leftEdgeNode.getFullBoundsReference().getMaxY());
			Point2D ballConnectionPoint = inactivationGateBallNode.getOffset();
			
			Shape stringShape = new Line2D.Double(channelConnectionPoint, ballConnectionPoint);
			inactivationGateString.setPathTo(stringShape); 
		}
		
		// Rotate based on the model element's orientation.
		channelLayer.setRotation(-membraneChannelModel.getRotationalAngle() + Math.PI / 2);
		edgeLayer.setRotation(-membraneChannelModel.getRotationalAngle() + Math.PI / 2);
		
		// If enabled, show/update the capture zone for this channel.
		if (SHOW_CAPTURE_ZONE){
			if (membraneChannelModel.getOpenness() > 0.1){
				// Show the capture node.
				captureZoneNode.setVisible(true);
				// Set the offset to the inverse of this node's offset, since
				// the capture zone maintains its own positioning info.
				captureZoneNode.setOffset(-channelLayer.getOffset().getX(), -channelLayer.getOffset().getY());
				captureZoneNode.setRotation(-channelLayer.getRotation());
			}
			if (membraneChannelModel.getOpenness() < 0.1){
				// Hide the capture zone node.
				captureZoneNode.setVisible(false);
			}
		}
	}	
}
