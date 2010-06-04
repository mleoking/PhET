/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A control that is meant for a Piccolo canvas that can be used for
 * controlling the zoom factor.
 * 
 * @author John Blanco
 */
public class ZoomControl extends PNode {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	private static final boolean SHOW_OUTLINE = false; // Generally used for debug.
	private static final Stroke STROKE = new BasicStroke(1f);
	private static final Color FILL_COLOR = Color.WHITE;
	private static final Color STROKE_COLOR = Color.BLACK;
	private static final Font SYMBOL_FONT = new PhetFont(14, true);
	private static final Color SYMBOL_COLOR = Color.BLUE;
	private static final double SLIDER_TRACK_WIDTH_PROPORTION = 0.25;
	private static final int NUM_TICK_MARKS_ON_TRACK = 10;
	private static final Stroke TICK_MARK_STROKE = new BasicStroke(1f);
	private static final Color TICK_MARK_COLOR = Color.LIGHT_GRAY;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	private double minZoom, maxZoom;
	private double buttonZoomAmt;
	private IZoomable zoomable;
	private PNode zoomInButton, zoomOutButton;
	private double sliderTrackHeight;
	private PNode sliderTrack;
	private double sliderKnobHeight;
	private PNode sliderKnob;
	private PhetPPath outline;
	
	private ZoomListener zoomListener = new ZoomListener() {
		public void zoomFactorChanged() {
			updateSliderKnobPosition();
		}
	};
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

	public ZoomControl(Dimension2D size, IZoomable zoomable, double minZoom, double maxZoom, int steps) {
	
		this.zoomable = zoomable;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.buttonZoomAmt = (maxZoom - minZoom) / (double)steps;
		
		// Add our zoom listener, which will update the slider position when
		// the zoom level changes.
		zoomable.addZoomListener(zoomListener);
		
		// Add the outline if it is enabled.  This is for debug.
		if (SHOW_OUTLINE){
			outline = new PhetPPath(new BasicStroke(2f), Color.RED);
			outline.setPathTo(new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight()));
			addChild(outline);
		}
		
		// Add the slider track.  It will have a listener so that the user can
		// click on it in order to set the zoom value.
		sliderTrackHeight = size.getHeight() - 2 * size.getWidth();
		double sliderTrackWidth = size.getWidth() * SLIDER_TRACK_WIDTH_PROPORTION;
		Shape sliderTrackShape = new Rectangle2D.Double(-sliderTrackWidth / 2, 0, sliderTrackWidth, sliderTrackHeight);
		sliderTrack = new PhetPPath(sliderTrackShape, FILL_COLOR, STROKE, STROKE_COLOR);
		double tickMarkWidth = sliderTrackWidth * 0.25;
		double interTickMarkVertDistance = sliderTrackHeight / (double)(NUM_TICK_MARKS_ON_TRACK + 1);
		for (int i = 0; i < NUM_TICK_MARKS_ON_TRACK; i++){
			PNode tickMark = new PhetPPath(new Line2D.Double(-tickMarkWidth / 2, 0, tickMarkWidth / 2, 0),
					TICK_MARK_STROKE, TICK_MARK_COLOR) {
			};
			tickMark.setOffset(0, (i + 1)*interTickMarkVertDistance);
			sliderTrack.addChild(tickMark);
		}
		sliderTrack.setOffset(size.getWidth() / 2, size.getWidth());
		sliderTrack.addInputEventListener( new CursorHandler(Cursor.HAND_CURSOR) );
		sliderTrack.addInputEventListener(new PBasicInputEventHandler(){
		    public void mouseReleased(final PInputEvent event) {
		    	ZoomControl.this.zoomable.setZoomFactor(trackPosToZoomFactor(event.getPositionRelativeTo(sliderTrack).getY()));
		    }
		});
		addChild(sliderTrack);
		
		// Add the slider knob.
		sliderKnobHeight = size.getWidth() * 0.3;
		Shape sliderKnobShape = new RoundRectangle2D.Double(-size.getWidth() / 2, -sliderKnobHeight / 2,
				size.getWidth(), sliderKnobHeight, 4, 4);
		sliderKnob = new PhetPPath(sliderKnobShape, FILL_COLOR, STROKE, STROKE_COLOR);
		sliderKnob.setOffset(size.getWidth() / 2, 0);  // Y position set by update method.
		sliderKnob.addInputEventListener( new CursorHandler(Cursor.N_RESIZE_CURSOR) );
        sliderKnob.addInputEventListener( new PDragEventHandler(){
            public void drag(PInputEvent event){
                handleSliderKnobDragEvent(event);
            }
        });
		addChild(sliderKnob);
		
		// Add the buttons for zooming in and out.
		// Note: The plus and minus symbols are not internationlized, and I'm
		// assuming that it isn't necessary to do so.  If it is ever needed,
		// this is where it would be fixed.
		zoomInButton = createZoomButton(size.getWidth(), '+');
		zoomInButton.setOffset(0, 0);
		zoomInButton.addInputEventListener(new PBasicInputEventHandler(){
			@Override
		    public void mouseReleased(final PInputEvent event) {
				// Zoom in by one increment.
				double currentZoomFactor = ZoomControl.this.zoomable.getZoomFactor();
				if (currentZoomFactor < ZoomControl.this.maxZoom){
					if (ZoomControl.this.maxZoom - currentZoomFactor > buttonZoomAmt){
						ZoomControl.this.zoomable.setZoomFactor(currentZoomFactor + buttonZoomAmt);
					}
					else{
						ZoomControl.this.zoomable.setZoomFactor(ZoomControl.this.maxZoom);
					}
				}
		    }
		});
		addChild(zoomInButton);
		
		zoomOutButton = createZoomButton(size.getWidth(), '-');
		zoomOutButton.setOffset(0, size.getHeight() - zoomOutButton.getBoundsReference().getHeight());
		zoomOutButton.addInputEventListener(new PBasicInputEventHandler(){
			@Override
		    public void mouseReleased(final PInputEvent event) {
				// Zoom out by one increment.
				double currentZoomFactor = ZoomControl.this.zoomable.getZoomFactor();
				if (currentZoomFactor > ZoomControl.this.minZoom){
					if (currentZoomFactor - ZoomControl.this.minZoom > buttonZoomAmt){
						ZoomControl.this.zoomable.setZoomFactor(currentZoomFactor - buttonZoomAmt);
					}
					else{
						ZoomControl.this.zoomable.setZoomFactor(ZoomControl.this.minZoom);
					}
				}
		    }
		});
		addChild(zoomOutButton);
		
		// Initialize slider knob position.
		updateSliderKnobPosition();
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	private void updateSliderKnobPosition(){
		sliderKnob.setOffset( sliderKnob.getOffset().getX(), zoomFactorToTrackPos(zoomable.getZoomFactor()) );
	}
	
    private void handleSliderKnobDragEvent(PInputEvent event){
        
        PNode draggedNode = event.getPickedNode();
        PDimension d = event.getDeltaRelativeTo(draggedNode);
        draggedNode.localToParent(d);
        double movementAmount = d.getHeight();
        
		double knobMovementRange = zoomOutButton.getFullBoundsReference().getMinY()
				- zoomInButton.getFullBoundsReference().getMaxY();
		
		double zoomDelta = -movementAmount / knobMovementRange * (maxZoom - minZoom);
		
		double newZoomValue = zoomable.getZoomFactor() + zoomDelta;

		newZoomValue = MathUtil.clamp(minZoom, newZoomValue, maxZoom);
		
		zoomable.setZoomFactor(newZoomValue);
    }
    
    private double trackPosToZoomFactor(double trackPosition){
    	// Set up some values needed for calculations.  Note that the max
    	// track position is associated with the min zoom, and vice versa,
    	// which can be a bit confusing.
    	double maxTrackPosition = sliderTrackHeight - sliderKnobHeight / 2;
    	double minTrackPostion = sliderKnobHeight / 2;
    	
    	// Limit the position to a value was can work with.
    	trackPosition = MathUtil.clamp(minTrackPostion, trackPosition, maxTrackPosition);
    	
    	// Do the math.
    	return (maxTrackPosition - trackPosition) / maxTrackPosition * (maxZoom - minZoom) + minZoom;
    }
    
    private double zoomFactorToTrackPos(double zoomFactor){
    	assert zoomFactor >= minZoom && zoomFactor <= maxZoom;
    	
    	double minPos = sliderTrackHeight + sliderTrack.getOffset().getY() - sliderKnobHeight / 2;
    	double maxPos = minPos - sliderTrackHeight + sliderKnobHeight;
    	
    	return minPos - ((zoomFactor - minZoom) / (maxZoom - minZoom)) * (minPos - maxPos);
    }
    
    /**
     * Create a zoom button.  This was created to avoid duplicated code, since
     * two of these are needed.
     * 
     * @param size - Size of the button, only one dimension because it is assumed to be square.
     * @param labelCharacter - Single character label.
     * @return - The button.
     */
    private PNode createZoomButton(double size, char labelCharacter){
		Shape zoomButtonShape = new RoundRectangle2D.Double(0, 0, size, size, 4, 4);
		PNode zoomButton = new PhetPPath(zoomButtonShape, FILL_COLOR, STROKE, STROKE_COLOR);
		zoomButton.addInputEventListener( new CursorHandler(Cursor.HAND_CURSOR) );
		PText zoomButtonLabel = new PText(String.valueOf(labelCharacter));
		zoomButtonLabel.setPickable(false);
		zoomButtonLabel.setFont(SYMBOL_FONT);
		zoomButtonLabel.setTextPaint(SYMBOL_COLOR);
		zoomButtonLabel.setScale(size * 0.5 / zoomButtonLabel.getFullBoundsReference().width);
		zoomButtonLabel.setOffset(size / 2 - zoomButtonLabel.getFullBoundsReference().width / 2, 
				size / 2 - zoomButtonLabel.getFullBoundsReference().height / 2);
		zoomButton.addChild(zoomButtonLabel);
		
		return zoomButton;
    }
}
