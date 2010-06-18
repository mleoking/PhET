/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.NeuronModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Class that portrays charge symbols, i.e. pluses ('+') and minuses ('-').
 */
public class ChargeSymbolNode extends PNode {
	
	private static final float STROKE_WIDTH = 0.75f;
	private static final Stroke EDGE_STROKE = new BasicStroke(STROKE_WIDTH);
	private static final Color EDGE_COLOR = new Color(255, 102, 0);
	private static final Color FILL_COLOR = Color.WHITE;
	
	// Factor that controls the thickness of the plus and minus sign, must be
	// less than 1.
	private static final double THICKNESS_FACTOR = 0.4;
	
	private final NeuronModel axonModel;
	private final double maxPotential;
	private final double maxWidth;
	private final boolean polarityReversed;
    private final PPath representation;

    /**
     * 
     * @param axonModel - Model where the potential is obtained.
     * @param maxWidth - Max width in screen coords, which also defines max height.
     * @param maxPotential - The potential at which the max size is shown.
     * @param polarityReversed - Whether the polarity is reversed, meaning that
     * a plus is shown for a negative value and vice versa. 
     */
    public ChargeSymbolNode( NeuronModel axonModel, double maxWidth, double maxPotential, boolean polarityReversed ) {
    	
		this.axonModel = axonModel;
		this.maxWidth = maxWidth;
		this.polarityReversed = polarityReversed;
		this.maxPotential = maxPotential;

        axonModel.addListener(new NeuronModel.Adapter(){
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
    	
    	double width = maxWidth * Math.abs((axonModel.getMembranePotential() / maxPotential));
    	
    	boolean drawPlusSymbol = (axonModel.getMembranePotential() > 0 && !polarityReversed) ||
    	                         (axonModel.getMembranePotential() < 0 && polarityReversed);
    	
    	if (drawPlusSymbol){
    		representation.setPathTo(drawPlusSign(width));
    	}
    	else{
    		representation.setPathTo(drawMinusSign(width));
    	}
    }
    
    private Shape drawPlusSign(double width){
    	DoubleGeneralPath path = new DoubleGeneralPath();
    	double thickness = width * THICKNESS_FACTOR;
    	double halfThickness = thickness / 2;
    	double halfWidth = width / 2;
    	path.moveTo(-halfWidth, -halfThickness);
    	path.lineTo(-halfWidth, halfThickness);
    	path.lineTo(-halfThickness, halfThickness);
    	path.lineTo(-halfThickness, halfWidth);
    	path.lineTo(halfThickness, halfWidth);
    	path.lineTo(halfThickness, halfThickness);
    	path.lineTo(halfWidth, halfThickness);
    	path.lineTo(halfWidth, -halfThickness);
    	path.lineTo(halfThickness, -halfThickness);
    	path.lineTo(halfThickness, -halfWidth);
    	path.lineTo(-halfThickness, -halfWidth);
    	path.lineTo(-halfThickness, -halfThickness);
    	path.closePath();
    	return path.getGeneralPath();
    }
    
    private Shape drawMinusSign(double width){
    	double height = width * THICKNESS_FACTOR;
    	return new Rectangle2D.Double(-width / 2, -height / 2 , width, height);
    }
}
