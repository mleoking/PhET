// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.AxonMembrane;
import edu.umd.cs.piccolo.PNode;

/**
 * Representation of the transverse cross section of the axon the view.
 * 
 * @author John Blanco
 */
public class AxonCrossSectionNode extends PNode {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	private static final Color MEMBRANE_COLOR = Color.YELLOW;
	private static final Stroke STROKE = new BasicStroke(2f);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ModelViewTransform2D mvt;
    private PhetPPath outerMembrane;
    private PhetPPath innerMembrane;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public AxonCrossSectionNode( AxonMembrane axonMembraneModel, ModelViewTransform2D transform ) {
    	
        this.mvt = transform;
        
        double outerDiameter = axonMembraneModel.getCrossSectionDiameter() + axonMembraneModel.getMembraneThickness();
        double innerDiameter = axonMembraneModel.getCrossSectionDiameter() - axonMembraneModel.getMembraneThickness();
        
        // Create the cross section, which consists of an outer circle that
        // represents the outer edge of the membrane and an inner circle that
        // represents the inner edge of the membrane and the inner portion of
        // the axon.
        Shape outerDiameterCircle = mvt.createTransformedShape(new Ellipse2D.Double(-outerDiameter / 2, -outerDiameter / 2, outerDiameter, outerDiameter));
        Shape innerDiameterCircle = mvt.createTransformedShape(new Ellipse2D.Double(-innerDiameter / 2, -innerDiameter / 2, innerDiameter, innerDiameter));
        outerMembrane = new PhetPPath( outerDiameterCircle, MEMBRANE_COLOR, STROKE, Color.BLACK);
		addChild( outerMembrane );
        innerMembrane = new PhetPPath( innerDiameterCircle, new Color(73, 210, 242),  STROKE, Color.BLACK);
		addChild( innerMembrane );		
	}
}
