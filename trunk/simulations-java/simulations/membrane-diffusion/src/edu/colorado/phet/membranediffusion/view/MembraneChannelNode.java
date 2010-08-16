/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.membranediffusion.model.CaptureZone;
import edu.colorado.phet.membranediffusion.model.MembraneChannel;
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
    
    // For debug.
    private static final boolean SHOW_CAPTURE_ZONES = false;
	
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
			public void positionChanged() {
				updateLocation();
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
		
		// If enabled, show the capture zones.
		if (SHOW_CAPTURE_ZONES){
		    // The capture zones contain their own position information
		    // inherent in their shape.  However, here we want the origin
		    // point of the capture zone to end up at position zero.  In order
		    // to compensate for this, we first use the MVT to transform the
		    // shape, which gives us a shape of the correct size, and then
		    // translate the shape such that its origin point is (0,0).
		    Point2D originPointInViewCoords = mvt.modelToView( membraneChannelModel.getCenterLocation() );
		    AffineTransform translateToOriginTransform = AffineTransform.getTranslateInstance( -originPointInViewCoords.getX(),
		            -originPointInViewCoords.getY() );

		    CaptureZone lowerCaptureZone = membraneChannelModel.getLowerCaptureZone();
		    Shape lowerZoneShape = lowerCaptureZone.getShape();
            Shape transformedLowerZoneShape = mvt.createTransformedShape( lowerZoneShape );
            Shape compensatedTransformedLowerZoneShape = translateToOriginTransform.createTransformedShape( transformedLowerZoneShape );
            
            CaptureZone upperCaptureZone = membraneChannelModel.getUpperCaptureZone();
            Shape upperZoneShape = upperCaptureZone.getShape();
            Shape transformedUpperZoneShape = mvt.createTransformedShape( upperZoneShape );
            Shape compensatedTransformedUpperZoneShape = translateToOriginTransform.createTransformedShape( transformedUpperZoneShape );

            // Add these zones to the node.
            channelLayer.addChild( new PhetPPath(compensatedTransformedLowerZoneShape, new BasicStroke(4), Color.GREEN) );
            channelLayer.addChild( new PhetPPath(compensatedTransformedUpperZoneShape, new BasicStroke(4), Color.RED) );
		}
		
		// Update the representation and location.
		updateRepresentation();
		updateLocation();
	}
	
	/**
	 * Add this node to the two specified parent nodes.  This is done in order
	 * to achieve a better layering effect that allows particles to look
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
	}
	
	public void removeFromCanvas(PNode channelLayer, PNode edgeLayer){
		channelLayer.removeChild(this.channelLayer);
		edgeLayer.removeChild(this.edgeLayer);
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
	
	private void updateLocation(){
		channelLayer.setOffset(mvt.modelToViewDouble(membraneChannelModel.getCenterLocation()));
		edgeLayer.setOffset(mvt.modelToViewDouble(membraneChannelModel.getCenterLocation()));
	}
	
	private void updateRepresentation(){
		
		// Set the channel width as a function of the openness of the membrane channel.
		double channelWidth = membraneChannelModel.getChannelSize().getWidth() * membraneChannelModel.getOpenness();
		Dimension2D channelSize = new PDimension(
				channelWidth,
				membraneChannelModel.getChannelSize().getHeight());
		Dimension2D transformedChannelSize = new PDimension(
				Math.abs(mvt.modelToViewDifferentialXDouble(channelSize.getWidth())),
				Math.abs(mvt.modelToViewDifferentialYDouble(channelSize.getHeight())));
		
		// Make the node a bit bigger than the channel so that the edges can
		// be placed over it with no gaps.
		float oversizeFactor = 1.1f;
		
		float width = (float)transformedChannelSize.getWidth() * oversizeFactor;
		float height = (float)transformedChannelSize.getHeight() * oversizeFactor;
		float edgeWidth = (float)leftEdgeNode.getFullBoundsReference().width; // Assume both edges are the same size.
		
		GeneralPath path = new GeneralPath();
		path.moveTo(0, 0);
		path.quadTo((width + edgeWidth) / 2, height / 8, width + edgeWidth, 0);
		path.lineTo(width + edgeWidth, height);
		path.quadTo((width + edgeWidth) / 2, height * 7 / 8, 0, height);
		path.closePath();
		
		channel.setPathTo(path);
		channel.setOffset(-channel.getFullBoundsReference().width / 2, -channel.getFullBoundsReference().height / 2);
		System.out.println("Channel bounds: " + channel.getFullBoundsReference());

		leftEdgeNode.setOffset(
				-transformedChannelSize.getWidth() / 2 - leftEdgeNode.getFullBoundsReference().width / 2, 0);
		
		rightEdgeNode.setOffset(
				transformedChannelSize.getWidth() / 2 + rightEdgeNode.getFullBoundsReference().width / 2, 0);
	}	
}
