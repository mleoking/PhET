/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

public class Glucose extends SimpleSugar {

	public Glucose(Point2D initialPosition) {
		super(initialPosition, Color.BLUE);
		// Add binding point for Galactose.
		addBindingPoint(new BindingPoint(ModelElementType.GALACTOSE,
				new PDimension(-getWidth()/2, 0)));
		// Add binding point for LacZ.
		addBindingPoint(new BindingPoint(ModelElementType.LAC_Z,
				new PDimension(-getWidth()/2, 0)));
	}
	
    public Glucose(double x,double y) {
        this(new Point2D.Double(x,y));
    }

	public Glucose(){
		this(new Point2D.Double());
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.GLUCOSE;
	}
}
