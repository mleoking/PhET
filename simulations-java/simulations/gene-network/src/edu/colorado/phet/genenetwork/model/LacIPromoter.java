/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class LacIPromoter extends SimpleModelElement {

	private static final double WIDTH = 10;  // Nanometers. 
	private static final double HEIGHT = 2.5;  // Nanometers.
	
	public LacIPromoter(Point2D initialPosition) {
		super(new RoundRectangle2D.Double(-WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT, 2, 2), new Point2D.Double(),
				new Color(112, 190, 237));
	}
	
    public LacIPromoter(double x,double y) {
        this(new Point2D.Double(x,y));
    }

	public LacIPromoter(){
		this(new Point2D.Double());
	}
	
	@Override
	SimpleElementType getType() {
		return SimpleElementType.LAC_I_PROMOTER;
	}
}
