/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class LacYGene extends SimpleModelElement {

	private static final double WIDTH = 20;  // Nanometers. 
	private static final double HEIGHT = 5;  // Nanometers.
	
	public LacYGene(Point2D initialPosition) {
		super(new RoundRectangle2D.Double(-WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT, 2, 2), new Point2D.Double(),
				new Color(138, 198, 118));
	}
	
    public LacYGene(double x,double y) {
        this(new Point2D.Double(x,y));
    }

	public LacYGene(){
		this(new Point2D.Double());
	}
	
	@Override
	SimpleElementType getType() {
		return SimpleElementType.LAC_Y_GENE;
	}
}
