/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LacIGene extends SimpleModelElement {

	private static final double WIDTH = 20;  // Nanometers. 
	private static final double HEIGHT = 5;  // Nanometers.
	
	public LacIGene(Point2D initialPosition) {
		super(new Rectangle2D.Double(-WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT), new Point2D.Double(), Color.DARK_GRAY);
	}
	
    public LacIGene(double x,double y) {
        this(new Point2D.Double(x,y));
    }

	public LacIGene(){
		this(new Point2D.Double());
	}
	
	@Override
	SimpleElementType getType() {
		return SimpleElementType.LAC_I_GENE;
	}
}
