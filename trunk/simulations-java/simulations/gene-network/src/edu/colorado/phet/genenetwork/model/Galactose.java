/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

public class Galactose extends SimpleSugar {

	public Galactose(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, initialPosition, Color.ORANGE);
		
		// Add attachment point for Glucose.
		addAttachmentPoint(new AttachmentPoint(ModelElementType.GLUCOSE,
				new PDimension(getWidth()/2, 0)));
		// Add attachment point for LacZ.
		addAttachmentPoint(new AttachmentPoint(ModelElementType.LAC_Z,
				new PDimension(getWidth()/2, 0)));
	}

    public Galactose(IObtainGeneModelElements model, double x, double y) {
        this(model, new Point2D.Double(x,y));
    }

	public Galactose(IObtainGeneModelElements model){
		this(model, new Point2D.Double());
	}
	
	public Galactose(){
		this(null);
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.GALACTOSE;
	}
}
