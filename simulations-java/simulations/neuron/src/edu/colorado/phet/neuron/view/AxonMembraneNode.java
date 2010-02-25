/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.AxonMembrane;
import edu.colorado.phet.neuron.model.AxonMembrane.TravelingActionPotential;
import edu.umd.cs.piccolo.PNode;

/**
 * Representation of the axon membrane in the view.
 * 
 * @author John Blanco
 */
public class AxonMembraneNode extends PNode {
	
	private static final Color AXON_BODY_COLOR = new Color(221, 216, 44);
	private static final Stroke STROKE = new BasicStroke(2f);
	private static final boolean SHOW_GRADIENT_LINE = false;
	
	private AxonMembrane axonMembraneModel;
    private ModelViewTransform2D mvt;
    private PhetPPath outerMembrane;
    private PhetPPath innerMembrane;
    private PhetPPath axonBody;
    private TravelingActionPotentialNode travelingActionPotentialNode;

    public AxonMembraneNode( AxonMembrane axonMembraneModel, ModelViewTransform2D transform ) {
		this.axonMembraneModel = axonMembraneModel;
        this.mvt = transform;
        
        // Listen to the axon membrane for events that matter to the visual
        // representation.
        axonMembraneModel.addListener(new AxonMembrane.Listener() {
			
			public void travelingActionPotentialStarted() {
				addTravelingActionPotentialNode(AxonMembraneNode.this.axonMembraneModel.getTravelingActionPotential());
			}
			
			public void travelingActionPotentialEnded() {
				removeTravelingActionPotentialNode();
			}
		});
        
        // Add the axon body.
        Shape axonBodyShape = mvt.createTransformedShape(axonMembraneModel.getAxonBodyShape());
        Rectangle2D axonBodyBounds = axonBodyShape.getBounds2D();
        Rectangle2D crossSectionBounds = mvt.createTransformedShape(axonMembraneModel.getCrossSectionEllipseShape()).getBounds2D();
        Point2D gradientOrigin = new Point2D.Double(axonBodyBounds.getMaxX(), axonBodyBounds.getMaxY());
        Point2D gradientExtent = new Point2D.Double(crossSectionBounds.getCenterX(), crossSectionBounds.getY());
        GradientPaint axonBodyGradient = new GradientPaint(
        		gradientOrigin,
        		ColorUtils.darkerColor(AXON_BODY_COLOR, 0.5),
        		gradientExtent,
        		ColorUtils.brighterColor(AXON_BODY_COLOR, 0.2));
        axonBody = new PhetPPath( axonBodyShape, axonBodyGradient, STROKE, Color.BLACK );
        addChild( axonBody );
        
        double outerDiameter = axonMembraneModel.getCrossSectionDiameter() + axonMembraneModel.getMembraneThickness();
        double innerDiameter = axonMembraneModel.getCrossSectionDiameter() - axonMembraneModel.getMembraneThickness();
        
        /*
         * TODO:
         * The cross section, for a while, had an image in the middle that was
         * intended to create the appearance of depth.  After seeing it, Noah
         * P thought that it could be misconstrued as some sort of charge
         * gradient, so it was removed.  This code should be kept for a while
         * until we are sure that having no image in the center (i.e. just a
         * solid color) works reasonably well.
        // Create the cross section, which consists of an outer circle that
        // represents the outer edge of the membrane, an inner graphic that
        // is supposed to look like the fluid inside the axon, and an inner
        // circle that represents the inner edge of the membrane.
        Shape outerDiameterCircle = mvt.createTransformedShape(new Ellipse2D.Double(-outerDiameter / 2, -outerDiameter / 2, outerDiameter, outerDiameter));
        Shape innerDiameterCircle = mvt.createTransformedShape(new Ellipse2D.Double(-innerDiameter / 2, -innerDiameter / 2, innerDiameter, innerDiameter));
        outerMembrane = new PhetPPath( outerDiameterCircle, Color.YELLOW, new BasicStroke(4), Color.BLACK);
		addChild( outerMembrane );
        BufferedImage bufferedImage = NeuronResources.getImage( "water_circle.png" );
        PImage centerImage = new PImage( bufferedImage );
        Rectangle bounds = innerDiameterCircle.getBounds();
        centerImage.setScale(bounds.getWidth() / centerImage.getFullBoundsReference().width);
        centerImage.setOffset(bounds.getCenterX() - centerImage.getFullBoundsReference().width / 2,
        		bounds.getCenterY() - centerImage.getFullBoundsReference().height / 2);
        addChild(centerImage);
        innerMembrane = new PhetPPath( innerDiameterCircle, new BasicStroke(4), Color.BLACK );
		addChild( innerMembrane );		
         */
        
        // Create the cross section, which consists of an outer circle that
        // represents the outer edge of the membrane and an inner circle that
        // represents the inner edge of the membrane and the inner portion of
        // the axon.
        Shape outerDiameterCircle = mvt.createTransformedShape(new Ellipse2D.Double(-outerDiameter / 2, -outerDiameter / 2, outerDiameter, outerDiameter));
        Shape innerDiameterCircle = mvt.createTransformedShape(new Ellipse2D.Double(-innerDiameter / 2, -innerDiameter / 2, innerDiameter, innerDiameter));
        outerMembrane = new PhetPPath( outerDiameterCircle, Color.YELLOW, STROKE, Color.BLACK);
		addChild( outerMembrane );
        innerMembrane = new PhetPPath( innerDiameterCircle, new Color(73, 210, 242),  STROKE, Color.BLACK);
		addChild( innerMembrane );		

		if (SHOW_GRADIENT_LINE){
			// The following line is useful when trying to debug the gradient.
			addChild(new PhetPPath(new Line2D.Double(gradientOrigin, gradientExtent)));
		}
	}
    
    /**
     * Add the node that will represent the traveling action potential.
     * 
     * @param travelingActionPotential
     */
    private void addTravelingActionPotentialNode(TravelingActionPotential travelingActionPotential){
    	this.travelingActionPotentialNode = new TravelingActionPotentialNode(travelingActionPotential, mvt);
    	addChild(travelingActionPotentialNode);
    }
    
    /**
     * Remove the node that was representing the traveling action potential.
     */
    private void removeTravelingActionPotentialNode(){
    	removeChild(travelingActionPotentialNode);
    	travelingActionPotentialNode = null;
    }
    
    /**
     * Class that visually represents the action potential that travels down
     * the membrane prior to reaching the cross section.
     */
    private static class TravelingActionPotentialNode extends PNode {
    	
    	private static Color BACKGROUND_COLOR = new Color(204, 102, 255);
    	private static Stroke backgroundStroke = new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    	private static Color FOREGROUND_COLOR = Color.YELLOW;
    	private static Stroke foregroundStroke = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    	
    	private AxonMembrane.TravelingActionPotential travelingActionPotential;
    	private ModelViewTransform2D mvt;
    	private PhetPPath background = new PhetPPath(backgroundStroke, BACKGROUND_COLOR);
    	private PhetPPath foreground = new PhetPPath(foregroundStroke, FOREGROUND_COLOR);
    	
    	public TravelingActionPotentialNode(AxonMembrane.TravelingActionPotential travelingActionPotential, ModelViewTransform2D mvt) {
    		
    		addChild(background);
    		addChild(foreground);
    		
    		this.travelingActionPotential = travelingActionPotential;
    		this.mvt = mvt;

    		// Listen to the action potential
    		travelingActionPotential.addListener(new AxonMembrane.TravelingActionPotential.Adapter(){
				public void shapeChanged() {
					updateShape();
				}
    		});
    		
    		// Set the initial shape.
    		updateShape();
		}
    	
    	private void updateShape(){
    		foreground.setPathTo(mvt.createTransformedShape(travelingActionPotential.getShape()));
    		background.setPathTo(mvt.createTransformedShape(travelingActionPotential.getShape()));
    	}
    }
}
