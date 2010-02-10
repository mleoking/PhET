/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
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
        axonBody = new PhetPPath( axonBodyShape, new Color(221, 216, 44), new BasicStroke(4), Color.BLACK );
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
        outerMembrane = new PhetPPath( outerDiameterCircle, Color.YELLOW, new BasicStroke(4), Color.BLACK);
		addChild( outerMembrane );
        innerMembrane = new PhetPPath( innerDiameterCircle, new Color(73, 210, 242),  new BasicStroke(4), Color.BLACK);
		addChild( innerMembrane );		
	}
    
    /**
     * Add the node that will represent the traveling action potential.
     * 
     * @param travelingActionPotential
     */
    private void addTravelingActionPotentialNode(TravelingActionPotential travelingActionPotential){
    	this.travelingActionPotentialNode = new TravelingActionPotentialNode(travelingActionPotential);
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
    private static class TravelingActionPotentialNode extends PhetPPath {
    	
    	private static Color COLOR = Color.YELLOW;
    	
    	private AxonMembrane.TravelingActionPotential travelingActionPotential;
    	
    	public TravelingActionPotentialNode(AxonMembrane.TravelingActionPotential travelingActionPotential) {
    		
    		super(COLOR);
    		
    		this.travelingActionPotential = travelingActionPotential;

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
    		setPathTo(travelingActionPotential.getShape());
    	}
    }
}
