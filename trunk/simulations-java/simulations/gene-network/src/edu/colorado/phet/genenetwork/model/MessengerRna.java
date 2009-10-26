/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Random;


/**
 * Class that represents messenger RNA in the model.
 * 
 * @author John Blanco
 */
public class MessengerRna extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = Color.BLACK;
	private static float APPROX_LENGTH = 30;  // In nanometers.
	private static float THICKNESS = 0.25f;  // In nanometers.
	private static int NUM_SEGEMENTS = 10;
	private static Random RAND = new Random();
	
	public MessengerRna(Point2D initialPosition) {
		super(createShape(), initialPosition, ELEMENT_PAINT);
	}
	
	public MessengerRna() {
		this(new Point2D.Double());
	}
	
	private static Shape createShape(){
		
		/*
		 * returns an odd shaded shape sort of thing.
		DoubleGeneralPath topLine = new DoubleGeneralPath();
		topLine.moveTo(-APPROX_LENGTH / 2, 0);
		topLine.moveToRelative(APPROX_LENGTH, 0);
		for (int i=0; i<NUM_SEGEMENTS; i++){
			if (RAND.nextBoolean()){
				topLine.lineToRelative(APPROX_LENGTH / NUM_SEGEMENTS, -0.9);
			}
			else{
				topLine.lineToRelative(APPROX_LENGTH / NUM_SEGEMENTS, 0.9);
			}
		}
		return topLine.getGeneralPath();
		 */
		
		/*
		return new Line2D.Double(0, 0, 20, 0);
		*/
		
		/*
		return new QuadCurve2D.Double(-10, 0, 0, 5, 10, 0);
		*/
		
		GeneralPath path = new GeneralPath();
		float curveHeight = 5;
		path.moveTo(-APPROX_LENGTH/2, 0);
		path.quadTo(0, curveHeight, APPROX_LENGTH/2, 0);
		path.lineTo(APPROX_LENGTH/2, -THICKNESS);
		path.quadTo(0, curveHeight - THICKNESS, -APPROX_LENGTH/2, -THICKNESS);
		path.closePath();
		return path;
		

		
		/*
		GeneralPath squiggly = new GeneralPath();
		squiggly.moveTo(-10, 0);
		for (int i=0; i<10; i++){
			squiggly.lineTo(i*5, 1);
		}
		return squiggly;
		*/
	}
}
