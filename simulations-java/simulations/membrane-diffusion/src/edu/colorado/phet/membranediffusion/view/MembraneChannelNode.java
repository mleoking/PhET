/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.membranediffusion.model.CaptureZone;
import edu.colorado.phet.membranediffusion.model.MembraneChannel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Node that represents a membrane channel in the view.  Because the membrane
 * channel has particles that pass through it, this class in generally placed
 * on two different layers on the canvas.  See the methods for adding and
 * removing from the canvas for details.
 * 
 * @author John Blanco
 */
public class MembraneChannelNode extends PNode {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
    // For debug.
    private static final boolean SHOW_CAPTURE_ZONES = false;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private MembraneChannel membraneChannel;
	private ModelViewTransform2D mvt;
	private PNode channelLayer;
	private PNode edgeLayer;
	private PPath channel;
	private PPath leftEdgeNode;
	private PPath rightEdgeNode;
	private CursorHandler cursorHandler = new CursorHandler(Cursor.HAND_CURSOR);
	private PDragEventHandler dragEventHandler = new PDragEventHandler(){

        public void startDrag(PInputEvent event){
            super.startDrag(event);
            membraneChannel.setUserControlled( true );
        }

        public void drag(PInputEvent event){
            // Move the node as indicated by the drag event.
            PNode draggedNode = event.getPickedNode();
            PDimension d = event.getDeltaRelativeTo(draggedNode);
            draggedNode.localToParent(d);
            double newPosX = membraneChannel.getCenterLocation().getX() + mvt.viewToModelDifferentialX( d.getWidth() );
            double newPosY = membraneChannel.getCenterLocation().getY() + mvt.viewToModelDifferentialY( d.getHeight() );
            membraneChannel.setCenterLocation( newPosX, newPosY );
        }
        
        public void endDrag( PInputEvent event ){
            super.endDrag(event);
            membraneChannel.setUserControlled( false );
        }
	};
	
    // Array of listeners.
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    
    // Timer for performing the removal animation.
    private RemovalAnimationTimer removalAnimationTimer;
    
    // Initial offset, used for the removal animation.
    private Point2D initialOffset = new Point2D.Double();
    	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	/**
	 * Constructor.  Note that the parent nodes are passed in so that the
	 * layering appearance can be better controlled.
	 */
	public MembraneChannelNode(MembraneChannel membraneChannelModel, ModelViewTransform2D mvt){

		this.membraneChannel = membraneChannelModel;
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
		
		// Add input event listeners to allow the user to move and remove the
		// membrane channel.  NOTE: These are added to the sub-layers rather
		// than to the node as a whole because the layers may be added to the
		// canvas separately in order to make it look better when the
		// particles pass through the channels.
		channelLayer.addInputEventListener( cursorHandler );
		channelLayer.addInputEventListener( dragEventHandler );
		edgeLayer.addInputEventListener( cursorHandler );
		edgeLayer.addInputEventListener( dragEventHandler );
		
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
		
		// Store the original offset for use by the removal animation.  One
		// of the two layers is used rather than the offset as a whole, since
		// the layers is actually how this node is moved around.
		initialOffset.setLocation( channelLayer.getOffset() );
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	public void addListener(Listener listener){
	    listeners.add(listener);
	}

	public void removeListener(Listener listener){
	    listeners.remove(listener);
	}
	
	private void notifyRemovalAnimationComplete(){
	    // Make a copy of the list before sending out notifications, since
	    // these notifications may cause others to deregister as listeners,
	    // which would lead to concurrent modification exceptions.
	    ArrayList<Listener> listenerListCopy = new ArrayList<Listener>( listeners );
	    for (Listener listener : listenerListCopy){
	        listener.removalAnimationComplete();
	    }
	}

	/**
	 * Add this node to the two specified parent nodes.  This is done in order
	 * to achieve a better layering effect that allows particles to look
	 * more like they are moving through the channel.  It is not absolutely
	 * necessary to use this method for this node - it can be added to the
	 * canvas like any other PNode, it just won't have the layering.
	 * 
	 * @param channelLayerOnCanvas
	 * @param edgeLayerOnCanvas
	 */
	public void addToCanvas(PNode channelLayerOnCanvas, PNode edgeLayerOnCanvas){
		channelLayerOnCanvas.addChild(this.channelLayer);
		edgeLayerOnCanvas.addChild(this.edgeLayer);
	}
	
	public void removeFromCanvas(PNode channelLayer, PNode edgeLayer){
		channelLayer.removeChild(this.channelLayer);
		edgeLayer.removeChild(this.edgeLayer);
	}
	
	public MembraneChannel getMembraneChannel(){
	    return membraneChannel;
	}
	
	/**
	 * Execute an animation sequence that makes it more obvious to the user
	 * that this
	 */
	public void startRemovalAnimation(){
	    // Create the animation timer in order to initiate the animation.
	    removalAnimationTimer = new RemovalAnimationTimer( this );
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
		channelLayer.setOffset(mvt.modelToViewDouble(membraneChannel.getCenterLocation()));
		edgeLayer.setOffset(mvt.modelToViewDouble(membraneChannel.getCenterLocation()));
	}
	
	private void updateRepresentation(){
		
		// Set the channel width as a function of the openness of the membrane channel.
		double channelWidth = membraneChannel.getChannelSize().getWidth() * membraneChannel.getOpenness();
		Dimension2D channelSize = new PDimension(
				channelWidth,
				membraneChannel.getChannelSize().getHeight());
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

		leftEdgeNode.setOffset(
				-transformedChannelSize.getWidth() / 2 - leftEdgeNode.getFullBoundsReference().width / 2, 0);
		
		rightEdgeNode.setOffset(
				transformedChannelSize.getWidth() / 2 + rightEdgeNode.getFullBoundsReference().width / 2, 0);
	}
	
	/**
	 * Clean up memory references so that no memory leaks occur.
	 */
	public void cleanup(){
	   edgeLayer.removeInputEventListener( cursorHandler ); 
	   edgeLayer.removeInputEventListener( dragEventHandler ); 
	   channelLayer.removeInputEventListener( cursorHandler ); 
	   channelLayer.removeInputEventListener( dragEventHandler );
	}
	
    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
	
	/**
	 * Timer class that animates the removal of a membrane channel.  This is
	 * used when the user drags a membrane channel off of the membrane and
	 * back to the tool box, or to any location outside of the particle
	 * chamber.
	 * 
	 * The animation has two major stages.  The first is to move the membrane
	 * channel node back to the toolbox, and the second is to shrink the node.
	 */
	private static class RemovalAnimationTimer extends Timer {

	    private static final int TIMER_DELAY = 30;          // Milliseconds between each animation step.
	    
	    private static final double DISTANCE_MOVED_PER_ANIMATION_STEP = 20;

	    private static final double SHRINKAGE_RATE = 0.80;   // Amount of size change per timer firing, smaller number
	    
	    private enum AnimationStage { MOVING_BACK_TO_TOOL_BOX, SHRINKING };
	    
	    private AnimationStage animationStage = AnimationStage.MOVING_BACK_TO_TOOL_BOX; 
	    
	    private MembraneChannelNode membraneChannelNode;
	    
        /**
         * Constructor.
         */
        public RemovalAnimationTimer( MembraneChannelNode node ) {
            super( TIMER_DELAY, null );
            this.membraneChannelNode = node;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if (animationStage == AnimationStage.MOVING_BACK_TO_TOOL_BOX){
                        double distanceToDest =
                            membraneChannelNode.channelLayer.getOffset().distance( membraneChannelNode.initialOffset );
                        if (distanceToDest < DISTANCE_MOVED_PER_ANIMATION_STEP){
                            membraneChannelNode.channelLayer.setOffset( membraneChannelNode.initialOffset );
                            membraneChannelNode.edgeLayer.setOffset( membraneChannelNode.initialOffset );
                            // Move to the next stage.
                            animationStage = AnimationStage.SHRINKING;
                        }
                        else{
                            // Move one step towards the destination.
                            double angle = Math.atan2( 
                                    membraneChannelNode.initialOffset.getY() - membraneChannelNode.channelLayer.getOffset().getY(),
                                    membraneChannelNode.initialOffset.getX() - membraneChannelNode.channelLayer.getOffset().getX() );
                            double newPosX = membraneChannelNode.channelLayer.getOffset().getX() + 
                                    DISTANCE_MOVED_PER_ANIMATION_STEP * Math.cos( angle );
                            double newPosY = membraneChannelNode.channelLayer.getOffset().getY() + 
                                    DISTANCE_MOVED_PER_ANIMATION_STEP * Math.sin( angle );
                            membraneChannelNode.channelLayer.setOffset( newPosX, newPosY );
                            membraneChannelNode.edgeLayer.setOffset( newPosX, newPosY );
                        }
                    }
                    if (animationStage == AnimationStage.SHRINKING){
                        stop();
                        membraneChannelNode.notifyRemovalAnimationComplete();
                    }
                }
            });
            
            // Start the timer running.
            start();
        }
	}
	
	public interface Listener {
	    void removalAnimationComplete();
	}
}
