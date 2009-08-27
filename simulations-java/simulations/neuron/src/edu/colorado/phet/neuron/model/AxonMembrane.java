/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.geom.Ellipse2D;

/**
 * Model representation for the axon membrane.  Represents it as a cross
 * section and a shape that is intended to look like the body of the axon
 * receding into the distance.
 * 
 * @author John Blanco
 */
public class AxonMembrane {
	
	private static final double DEFAULT_MEMBRANE_THICKNESS = 7;  // In nanometers, obtained from web research.
	private static final double DEFAULT_DIAMETER = 80; // In nanometers.
	
	// Shape of the cross section of the membrane.
	private Ellipse2D crossSectionEllipseShape = new Ellipse2D.Double(-DEFAULT_DIAMETER / 2, -DEFAULT_DIAMETER / 2,
			DEFAULT_DIAMETER, DEFAULT_DIAMETER);
	
	public Ellipse2D getCrossSectionEllipseShape() {
		return crossSectionEllipseShape;
	}

	public void setCrossSectionEllipseShape(Ellipse2D crossSectionEllipseShape) {
		this.crossSectionEllipseShape = crossSectionEllipseShape;
	}
	
	public double getMembraneThickness(){
		return DEFAULT_MEMBRANE_THICKNESS;
	}
	
	public double getCrossSectionDiameter(){
		return DEFAULT_DIAMETER;
	}
	
}
