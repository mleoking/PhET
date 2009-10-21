package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LactoseModelElement extends SimpleModelElement{

	private static final Shape ELEMENT_SHAPE = new Rectangle2D.Double(-1, -1, 2, 2);
	private static final Paint ELEMENT_PAINT = Color.GREEN;

	public LactoseModelElement(Point2D initialPosition) {
		super(ELEMENT_SHAPE, initialPosition, ELEMENT_PAINT);
	}
	
	public LactoseModelElement() {
		this(new Point2D.Double(0, 0));
	}
}
