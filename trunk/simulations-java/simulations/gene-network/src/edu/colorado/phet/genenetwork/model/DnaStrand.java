/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a strand of DNA in the model, which includes its
 * size, shape, location, and the locations of genes upon it.
 * 
 * @author John Blanco
 */
public class DnaStrand {

	private DoubleGeneralPath strand1Shape = new DoubleGeneralPath();
	private Line2D strand2Shape;
	private final Dimension2D size;
	private Point2D position = new Point2D.Double();
	
	public DnaStrand(Dimension2D size, Point2D initialPosition){
		this.size = new PDimension(size);
		setPosition(initialPosition);
		
		updateShape();
	}
	
	public Point2D getPositionRef() {
		return position;
	}

	public void setPosition(double x, double y) {
		position.setLocation(x, y);
	}
	
	public void setPosition(Point2D newPosition) {
		setPosition(newPosition.getX(), newPosition.getY());
	}

	public Dimension2D getSize() {
		return size;
	}
	
	public Shape getStrand1Shape(){
		return strand1Shape.getGeneralPath();
	}
	
	private void updateShape(){
		strand1Shape.moveTo(-size.getWidth() / 2, 0);
		strand1Shape.lineTo(size.getWidth() / 2, 0);
	}
}
