/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class LacIPromoter extends SimpleModelElement {

	private static final double WIDTH = 10;  // Nanometers. 
	private static final double HEIGHT = 2.5;  // Nanometers.
	
	public LacIPromoter(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, new RoundRectangle2D.Double(-WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT, 1, 1),
				new Point2D.Double(), new Color(112, 190, 237), false, Double.POSITIVE_INFINITY);
	}
	
    public LacIPromoter(IGeneNetworkModelControl model, double x, double y) {
        this(model, new Point2D.Double(x,y));
    }

	public LacIPromoter(IGeneNetworkModelControl model){
		this(model, new Point2D.Double());
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_I_PROMOTER;
	}
	
	@Override
	public boolean isPartOfDnaStrand() {
		return true;
	}
}
