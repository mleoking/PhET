/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
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

	private static final boolean SHOW_OUTLINE = true; // Generally used for debug.
	private static final Stroke STROKE = new BasicStroke(1f);
	private static final Color FILL_COLOR = Color.WHITE;
	private static final Color STROKE_COLOR = Color.BLACK;
	private static final Font SYMBOL_FONT = new PhetFont(14, true);
	private static final Color SYMBOL_COLOR = Color.BLUE;
	private static final double SLIDER_TRACK_WIDTH_PROPORTION = 0.2;
	
	private double minZoom, maxZoom;
	private double buttonZoomAmt;
	private IZoomable zoomable;
	private PNode zoomInButton, zoomOutButton;
	private PNode sliderTrack;
	private double sliderKnobHeight;
	private PNode sliderKnob;
	private PhetPPath outline;
	
	private ZoomListener zoomListener = new ZoomListener() {
		public void zoomFactorChanged() {
			updateSliderKnobPosition();
		}
	};
	
	public ZoomControl(Dimension2D size, IZoomable zoomable, double minZoom, double maxZoom, int steps) {
	
		this.zoomable = zoomable;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.buttonZoomAmt = (maxZoom - minZoom) / (double)steps;
		
		// Add our zoom listener, which will update the slider position then
		// the zoom level changes.
		zoomable.addZoomListener(zoomListener);
		
		// Add the outline if it is enabled.
		if (SHOW_OUTLINE){
			outline = new PhetPPath(new BasicStroke(2f), Color.RED);
			outline.setPathTo(new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight()));
			addChild(outline);
		}
		
		// Add the slider track.
		double sliderTrackWidth = size.getWidth() * SLIDER_TRACK_WIDTH_PROPORTION;
		Shape sliderTrackShape = new Rectangle2D.Double(-sliderTrackWidth / 2, 0, sliderTrackWidth,
				size.getHeight() - size.getWidth());
		sliderTrack = new PhetPPath(sliderTrackShape, FILL_COLOR, STROKE, STROKE_COLOR);
		sliderTrack.setOffset(size.getWidth() / 2, size.getWidth() / 2);
		addChild(sliderTrack);
		
		// Add the slider knob.
		sliderKnobHeight = size.getWidth() * 0.3;
		Shape sliderKnobShape = new RoundRectangle2D.Double(-size.getWidth() / 2, -sliderKnobHeight / 2,
				size.getWidth(), sliderKnobHeight, 4, 4);
		sliderKnob = new PhetPPath(sliderKnobShape, FILL_COLOR, STROKE, STROKE_COLOR);
		sliderKnob.setOffset(size.getWidth() / 2, 0);  // Y position set by update method.
		sliderKnob.addInputEventListener( new CursorHandler(Cursor.N_RESIZE_CURSOR) );
        sliderKnob.addInputEventListener( new PDragEventHandler(){
            
            public void startDrag( PInputEvent event) {
                super.startDrag(event);
                System.out.println("Start drag");
            }
            
            public void drag(PInputEvent event){
                System.out.println("Drag");
                handleMouseDragEvent(event);
            }
            
            public void endDrag( PInputEvent event ){
                super.endDrag(event);     
                System.out.println("End drag");
            }
        });


		addChild(sliderKnob);
		
		// Add the buttons for zooming in and out.
		Shape zoomButtonShape = new RoundRectangle2D.Double(0, 0, size.getWidth(), size.getWidth(), 4, 4);
		zoomInButton = new PhetPPath(zoomButtonShape, FILL_COLOR, STROKE, STROKE_COLOR);
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
		// TODO: i18n of plus and minus symbols?
		PText zoomInLabel = new PText("+");
		zoomInLabel.setFont(SYMBOL_FONT);
		zoomInLabel.setTextPaint(SYMBOL_COLOR);
		zoomInLabel.setScale(size.getWidth() * 0.5 / zoomInLabel.getFullBoundsReference().width);
		zoomInLabel.setOffset(size.getWidth() / 2 - zoomInLabel.getFullBoundsReference().width / 2, 
				size.getWidth() / 2 - zoomInLabel.getFullBoundsReference().height / 2);
		zoomInButton.addChild(zoomInLabel);
		addChild(zoomInButton);
		
		zoomOutButton = new PhetPPath(zoomButtonShape, FILL_COLOR, STROKE, STROKE_COLOR);
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
		PText zoomOutLabel = new PText("-");
		zoomOutLabel.setFont(SYMBOL_FONT);
		zoomOutLabel.setTextPaint(SYMBOL_COLOR);
		zoomOutLabel.setScale(size.getWidth() * 0.5 / zoomOutLabel.getFullBoundsReference().width);
		zoomOutLabel.setOffset(size.getWidth() / 2 - zoomOutLabel.getFullBoundsReference().width / 2, 
				size.getWidth() / 2 - zoomOutLabel.getFullBoundsReference().height / 2);
		zoomOutButton.addChild(zoomOutLabel);
		addChild(zoomOutButton);
		
		// Initialize slider knob position.
		updateSliderKnobPosition();
	}
	
	private void updateSliderKnobPosition(){
		double minY = zoomInButton.getFullBoundsReference().getMaxY() + sliderKnobHeight / 2;
		double maxY = zoomOutButton.getFullBoundsReference().getMinY() - sliderKnobHeight / 2;
		double currentZoomProportion = zoomable.getZoomFactor() / ( maxZoom - minZoom ); 
		sliderKnob.setOffset(sliderKnob.getOffset().getX(), maxY - currentZoomProportion * (maxY - minY));
	}
	
    private void handleMouseDragEvent(PInputEvent event){
        
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
}
