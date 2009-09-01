/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Model representation for the axon membrane.  Represents it as a cross
 * section and a shape that is intended to look like the body of the axon
 * receding into the distance.
 * 
 * @author John Blanco
 */
public class AxonMembrane {

	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	// For now, and unless there is some reason to do otherwise, the center
	// of the cross section is positioned at the origin.
	private static final Point2D CROSS_SECTION_CENTER = new Point2D.Double(0, 0);

	// Fixed membrane characteristics.
	private static final double DEFAULT_MEMBRANE_THICKNESS = 4;  // In nanometers, obtained from web research.
	private static final double DEFAULT_DIAMETER = 80; // In nanometers.
	private static final double BODY_LENGTH = DEFAULT_DIAMETER * 1.5;
	private static final double BODY_TILT_ANGLE = Math.toRadians(45);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	// Shape of the cross section of the membrane.
	private Ellipse2D crossSectionEllipseShape = new Ellipse2D.Double(-DEFAULT_DIAMETER / 2, -DEFAULT_DIAMETER / 2,
			DEFAULT_DIAMETER, DEFAULT_DIAMETER);
	
	// Shape of the body of the axon.
	private Shape bodyShape;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	
	public AxonMembrane() {
		bodyShape = createAxonBodyShape();
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

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
	
	public Shape getAxonBodyShape(){
		return bodyShape;
	}
	
    /**
     * Create the shape of the axon body based on the size and position of the
     * cross section and some other fixed parameters.  This is a 2D shape that
     * is intended to look like a receeding 3D shape when presented in the
     * view, so its creation is a little unusual.
     */
    private Shape createAxonBodyShape(){
    	
    	GeneralPath axonBodyShape = new GeneralPath();
    	
    	// Find the two points at which the shape will intersect the outer
    	// edge of the cross section.
    	Point2D vanishingPoint = new Point2D.Double(BODY_LENGTH * Math.cos(BODY_TILT_ANGLE),
    			BODY_LENGTH * Math.sin(BODY_TILT_ANGLE));
    	double r = getCrossSectionDiameter() / 2 + getMembraneThickness() / 2;
    	double theta = BODY_TILT_ANGLE - Math.toRadians(90);
    	Point2D intersectionPointA = new Point2D.Double(r * Math.cos(theta), r * Math.sin(theta));
    	theta += Math.PI;
    	Point2D intersectionPointB = new Point2D.Double(r * Math.cos(theta), r * Math.sin(theta));
    	
    	axonBodyShape.moveTo(intersectionPointA.getX(), intersectionPointA.getY());
    	axonBodyShape.lineTo(intersectionPointB.getX(), intersectionPointB.getY());
    	axonBodyShape.lineTo(vanishingPoint.getX(), vanishingPoint.getY());
    	axonBodyShape.lineTo(intersectionPointA.getX(), intersectionPointA.getY());
    	
    	return axonBodyShape;
    	
    	
    }
}
