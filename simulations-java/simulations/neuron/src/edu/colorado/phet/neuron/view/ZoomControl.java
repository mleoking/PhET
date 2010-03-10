/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A control that is meant for a Piccolo canvas that can be used for
 * controlling the zoom factor.
 * 
 * @author John Blanco
 */
public class ZoomControl extends PNode {

	private static final boolean SHOW_OUTLINE = true; // Generally used for debug.
	private static final Stroke STROKE = new BasicStroke(1f);
	private static final Font SYMBOL_FONT = new PhetFont(14, true);
	private static final Color SYMBOL_COLOR = Color.BLUE;
	
	private PhetPPath outline;
	private double minZoom, maxZoom;
	private double buttonZoomAmt;
	private PNode zoomInButton, zoomOutButton;
	private IZoomable zoomable;
	
	public ZoomControl(Dimension2D size, IZoomable zoomable, double minZoom, double maxZoom, int steps) {
	
		this.zoomable = zoomable;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.buttonZoomAmt = (maxZoom - minZoom) / (double)steps;
		
		// Add the outline if it is enabled.
		if (SHOW_OUTLINE){
			outline = new PhetPPath(new BasicStroke(2f), Color.RED);
			outline.setPathTo(new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight()));
			addChild(outline);
		}
		
		// Add the buttons for zooming in and out.
		Shape zoomButtonShape = new RoundRectangle2D.Double(0, 0, size.getWidth(), size.getWidth(), 4, 4);
		zoomInButton = new PhetPPath(zoomButtonShape, Color.WHITE, STROKE, Color.BLACK);
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
		
		zoomOutButton = new PhetPPath(zoomButtonShape, Color.WHITE, STROKE, Color.BLACK);
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
	}
}
