/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

public class Glucose extends SimpleSugar {

	public Glucose(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, Color.BLUE);
		// Add attachment point for Galactose.
		addAttachmentPoint(new AttachmentPoint(ModelElementType.GALACTOSE,
				new PDimension(-getWidth()/2, 0)));
		// Add attachment point for LacZ.
		addAttachmentPoint(new AttachmentPoint(ModelElementType.LAC_Z,
				new PDimension(-getWidth()/2, 0)));
	}
	
    public Glucose(IGeneNetworkModelControl model, double x,double y) {
        this(model, new Point2D.Double(x,y));
    }

	public Glucose(IGeneNetworkModelControl model){
		this(model, new Point2D.Double());
	}
	
	public Glucose(){
		this(null);
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.GLUCOSE;
	}
}
