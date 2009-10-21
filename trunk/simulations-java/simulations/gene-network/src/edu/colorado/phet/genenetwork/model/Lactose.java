package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Lactose extends CompositeModelElement{
     public Galactose galactose;
    public Glucose glucose;

    public Lactose(double offsetX,double offsetY) {
        glucose = new Glucose(new Point2D.Double(-Galactose.HEIGHT/2 + offsetX, offsetY));
        add(glucose);
        galactose = new Galactose(new Point2D.Double(Galactose.HEIGHT/2 + offsetX, offsetY));
        add(galactose);
	}
}
