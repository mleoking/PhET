/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class LacZGene extends SimpleModelElement {

	private static final double WIDTH = 20;  // Nanometers.
	private static final double HEIGHT = 4;  // Nanometers.
	
	public LacZGene(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, new RoundRectangle2D.Double(-WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT, 1, 1),
				new Point2D.Double(), new Color(204, 171, 202));
	}
	
    public LacZGene(IObtainGeneModelElements model, double x, double y) {
        this(model, new Point2D.Double(x,y));
    }

	public LacZGene(IObtainGeneModelElements model){
		this(model, new Point2D.Double());
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_Z_GENE;
	}
	
	@Override
	public String getLabel() {
		// TODO: i18n
		return "LacZ Gene";
	}

	@Override
	public boolean isPartOfDnaStrand() {
		return true;
	}
}
