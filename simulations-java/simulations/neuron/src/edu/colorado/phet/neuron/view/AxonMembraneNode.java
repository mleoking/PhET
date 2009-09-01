/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.AxonMembrane;
import edu.umd.cs.piccolo.PNode;

/**
 * Representation of the axon membrane in the view.
 * 
 * @author John Blanco
 */
public class AxonMembraneNode extends PNode {
	
	AxonMembrane axonMembraneModel;
    private ModelViewTransform2D mvt;
    private PhetPPath outerMembrane;
    private PhetPPath innerMembrane;
    private PhetPPath axonBody;

    public AxonMembraneNode( AxonMembrane axonMembraneModel, ModelViewTransform2D transform ) {
		this.axonMembraneModel = axonMembraneModel;
        this.mvt = transform;
        
        // Add the axon body.
        Shape axonBodyShape = mvt.createTransformedShape(axonMembraneModel.getAxonBodyShape());
        axonBody = new PhetPPath( axonBodyShape, new Color(221, 216, 44), new BasicStroke(4), Color.BLACK );
        addChild( axonBody );
        
        double outerDiameter = axonMembraneModel.getCrossSectionDiameter() + axonMembraneModel.getMembraneThickness();
        double innerDiameter = axonMembraneModel.getCrossSectionDiameter() - axonMembraneModel.getMembraneThickness();
        
        // Add the cross section.
        Shape outerDiameterEllipse = mvt.createTransformedShape(new Ellipse2D.Double(-outerDiameter / 2, -outerDiameter / 2, outerDiameter, outerDiameter));
        Shape innerDiameterEllipse = mvt.createTransformedShape(new Ellipse2D.Double(-innerDiameter / 2, -innerDiameter / 2, innerDiameter, innerDiameter));
        outerMembrane = new PhetPPath( outerDiameterEllipse, Color.YELLOW, new BasicStroke(4), Color.BLACK);
		addChild( outerMembrane );
        innerMembrane = new PhetPPath( innerDiameterEllipse, new Color(190, 240, 239),  new BasicStroke(4), Color.BLACK);
		addChild( innerMembrane );
	}
}
