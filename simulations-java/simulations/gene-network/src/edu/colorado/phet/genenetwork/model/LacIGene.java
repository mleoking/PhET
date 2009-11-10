/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class LacIGene extends SimpleModelElement {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final double WIDTH = 20;  // Nanometers. 
	private static final double HEIGHT = 4;  // Nanometers.
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
	public LacIGene(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, new RoundRectangle2D.Double(-WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT, 1, 1),
				new Point2D.Double(), new Color(167, 167, 167));
	}
	
    public LacIGene(IObtainGeneModelElements model, double x, double y) {
        this(model, new Point2D.Double(x,y));
    }

	public LacIGene(IObtainGeneModelElements model){
		this(model, new Point2D.Double());
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_I_GENE;
	}

	@Override
	public String getLabel() {
		// TODO: i18n
		return "LacI Gene";
	}
}
