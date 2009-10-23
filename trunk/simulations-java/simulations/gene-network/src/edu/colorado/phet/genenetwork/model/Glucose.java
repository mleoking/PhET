/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Glucose extends SimpleSugar {

	public Glucose(Point2D initialPosition) {
		super(initialPosition, Color.BLUE);
	}
	
    public Glucose(double x,double y) {
        this(new Point2D.Double(x,y));
    }

	public Glucose(){
		this(new Point2D.Double());
	}
}
