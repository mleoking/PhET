/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
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

	@Override
	SimpleElementType getType() {
		return SimpleElementType.MESSENGER_RNA;
	}
	
	static Shape createShape(){
		
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
		
		/*
		 * Creates a simple curve.
		GeneralPath path = new GeneralPath();
		float curveHeight = 5;
		path.moveTo(-APPROX_LENGTH/2, 0);
		path.quadTo(0, curveHeight, APPROX_LENGTH/2, 0);
		path.lineTo(APPROX_LENGTH/2, -THICKNESS);
		path.quadTo(0, curveHeight - THICKNESS, -APPROX_LENGTH/2, -THICKNESS);
		path.closePath();
		return path;
		 */

		// Create the set of points that will define the curve.
		ArrayList<Point2DFloat> curvePoints = new ArrayList<Point2DFloat>();
		float curveHeight = 4;
		curvePoints.add(new Point2DFloat(-APPROX_LENGTH/2, 0));
		curvePoints.add(new Point2DFloat(-APPROX_LENGTH/3, curveHeight));
		curvePoints.add(new Point2DFloat(-APPROX_LENGTH/6, -curveHeight));
		curvePoints.add(new Point2DFloat(0, curveHeight/4));
		curvePoints.add(new Point2DFloat(APPROX_LENGTH/6, curveHeight));
		curvePoints.add(new Point2DFloat(APPROX_LENGTH/3, -curveHeight));
		curvePoints.add(new Point2DFloat(APPROX_LENGTH/2, 0));
		
		// Create the path.  Note that in order to create a closed shape, the
		// top line is drawn, then the line is essentially reversed but a
		// little lower, and the two lines are connected.
		GeneralPath path = new GeneralPath();
		path.moveTo(curvePoints.get(0).getX(), curvePoints.get(0).getY());
		path.curveTo(curvePoints.get(1).getX(), curvePoints.get(1).getY(), curvePoints.get(2).getX(),
				curvePoints.get(2).getY(), curvePoints.get(3).getX(), curvePoints.get(3).getY());
		path.curveTo(curvePoints.get(4).getX(), curvePoints.get(4).getY(), curvePoints.get(5).getX(),
				curvePoints.get(5).getY(), curvePoints.get(6).getX(), curvePoints.get(6).getY());
		
		path.lineTo(curvePoints.get(6).getX(), curvePoints.get(6).getY() - THICKNESS);
		path.curveTo(curvePoints.get(5).getX(), curvePoints.get(5).getY() - THICKNESS, curvePoints.get(4).getX(),
				curvePoints.get(4).getY() - THICKNESS, curvePoints.get(3).getX(), 
				curvePoints.get(3).getY() - THICKNESS);
		path.curveTo(curvePoints.get(2).getX(), curvePoints.get(2).getY() - THICKNESS, curvePoints.get(1).getX(),
				curvePoints.get(1).getY() - THICKNESS, curvePoints.get(0).getX(), 
				curvePoints.get(0).getY() - THICKNESS);
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
	
	private static class Point2DFloat{
		private float x;
		private float y;
		
		public Point2DFloat(float x, float y) {
			super();
			this.x = x;
			this.y = y;
		}

		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}
	}
}
