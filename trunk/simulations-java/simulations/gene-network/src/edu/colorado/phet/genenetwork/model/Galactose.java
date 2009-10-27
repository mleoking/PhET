/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

public class Galactose extends SimpleSugar {

	public Galactose(Point2D initialPosition) {
		super(initialPosition, Color.ORANGE);
		
		// Add binding point for Glucose.
		addBindingPoint(new BindingPoint(SimpleElementType.GLUCOSE,
				new PDimension(-HEIGHT/2 * (1 + Math.cos(Math.PI/3)), 0)));
	}

    public Galactose(double x,double y) {
        this(new Point2D.Double(x,y));
    }

	public Galactose(){
		this(new Point2D.Double());
	}
	
	@Override
	SimpleElementType getType() {
		return SimpleElementType.GALACTOSE;
	}
}
