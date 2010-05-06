/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Class that portrays charge symbols, i.e. pluses ('+') and minuses ('-').
 */
public class ChargeSymbolNode extends PNode {
	
	private static final float STROKE_WIDTH = 1;
	private static final Stroke EDGE_STROKE = new BasicStroke(STROKE_WIDTH);
	private static final Color EDGE_COLOR = Color.ORANGE;
	private static final Color FILL_COLOR = Color.WHITE;
	
	private final AxonModel axonModel;
	private final double maxPotential;
	private final double maxWidth;
    private final PPath representation;

    /**
     * 
     * @param axonModel - Model where the potential is obtained.
     * @param maxWidth - Max width in screen coords, which also defines max height.
     * @param maxPotential - The potential at which the max size is shown.
     * @param polarityReversed - Whether the polarity is reversed, meaning that
     * a plus is shown for a negative value and vice versa. 
     */
    public ChargeSymbolNode( AxonModel axonModel, double maxWidth, double maxPotential, boolean polarityReversed ) {
    	
		this.axonModel = axonModel;
		this.maxWidth = maxWidth;
		this.maxPotential = maxPotential;

        axonModel.addListener(new AxonModel.Adapter(){
    		public void membranePotentialChanged() {
    			updateRepresentation();
    		}
        });

        // Create the shape that represents this particle.
        representation = new PhetPPath(FILL_COLOR, EDGE_STROKE, EDGE_COLOR);
		addChild( representation );
        updateRepresentation();
	}
    
    private void updateRepresentation() {
    	System.out.println("Warning: updateRepresentation faked.");
    	double width = maxWidth * Math.abs((axonModel.getMembranePotential() / maxPotential));
    	representation.setPathTo(new Ellipse2D.Double(-width / 2, -width / 2, 
    			width, width));
    	if (axonModel.getMembranePotential() < 0){
    		representation.setPaint(Color.BLACK);
    	}
    	else{
    		representation.setPaint(FILL_COLOR);
    	}
    }
}
