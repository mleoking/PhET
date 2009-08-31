/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.AxonMembrane;
import edu.colorado.phet.neuron.model.Atom;
import edu.colorado.phet.neuron.module.NeuronDefaults;
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

    public AxonMembraneNode( AxonMembrane axonMembraneModel, ModelViewTransform2D transform ) {
		this.axonMembraneModel = axonMembraneModel;
        this.mvt = transform;

        double outerDiameter = axonMembraneModel.getCrossSectionDiameter() + axonMembraneModel.getMembraneThickness() / 2;
        double innerDiameter = axonMembraneModel.getCrossSectionDiameter() - axonMembraneModel.getMembraneThickness() / 2;
        
        Shape outerDiameterEllipse = mvt.createTransformedShape(new Ellipse2D.Double(-outerDiameter / 2, -outerDiameter / 2, outerDiameter, outerDiameter));
        Shape innerDiameterEllipse = mvt.createTransformedShape(new Ellipse2D.Double(-innerDiameter / 2, -innerDiameter / 2, innerDiameter, innerDiameter));
        outerMembrane = new PhetPPath( outerDiameterEllipse, Color.YELLOW, new BasicStroke(4), Color.BLACK);
		addChild( outerMembrane );
        innerMembrane = new PhetPPath( innerDiameterEllipse, Color.PINK,  new BasicStroke(4), Color.BLACK);
		addChild( innerMembrane );
	}
}
