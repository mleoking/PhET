/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.Stroke;
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
 * Representation of the axon membrane body in the view.  This is the part
 * that the action potential travels along, and is supposed to look sort of
 * 3D.
 * 
 * @author John Blanco
 */
public class AxonBodyNode extends PNode {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	private static final Color AXON_BODY_COLOR = new Color(221, 216, 44);
	private static final Stroke STROKE = new BasicStroke(2f);
	private static final boolean SHOW_GRADIENT_LINE = false;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	private AxonMembrane axonMembraneModel;
    private ModelViewTransform2D mvt;
    private PhetPPath axonBody;
    private TravelingActionPotentialNode travelingActionPotentialNode;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public AxonBodyNode( AxonMembrane axonMembraneModel, ModelViewTransform2D transform ) {
		this.axonMembraneModel = axonMembraneModel;
        this.mvt = transform;
        
        // Listen to the axon membrane for events that matter to the visual
        // representation.
        axonMembraneModel.addListener(new AxonMembrane.Adapter() {
			
			public void travelingActionPotentialStarted() {
				addTravelingActionPotentialNode(AxonBodyNode.this.axonMembraneModel.getTravelingActionPotential());
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
        
		if (SHOW_GRADIENT_LINE){
			// The following line is useful when trying to debug the gradient.
			addChild(new PhetPPath(new Line2D.Double(gradientOrigin, gradientExtent)));
		}
	}
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

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
    
    //----------------------------------------------------------------------------
    // Inner Classes, Interfaces, etc.
    //----------------------------------------------------------------------------

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
