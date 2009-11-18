/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Class that represents messenger RNA in the model.
 * 
 * NOTE: For the purposes of this sim, it is assume that the messenger RNA is
 * always fairly horizontal in the model, and never vertical.
 * 
 * @author John Blanco
 */
public class MessengerRna extends SimpleModelElement {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	// Define the appearance.
	private static final Paint ELEMENT_PAINT = Color.BLACK;
	private static float THICKNESS = 0.25f;  // In nanometers.
	
	// Default initial length, used if none is specified.
	private static float DEFAULT_LENGTH = 30;  // In nanometers.
	
	// Default length of the segments used to create the initial shape.
	private static float DEFAULT_SEGMENT_LENGTH = 3;

	// Used so that every strand looks a little different.
	private static final Random RAND = new Random();
	
	// Value that determines how long this exists before starting to fade.
	private static final double PRE_FADE_EXISTENCE_TIME = 10; // In seconds.

	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------
	
	private double length = 0;
	private ArrayList<Point2D> points = new ArrayList<Point2D>();
	private ExistenceState existenceState = ExistenceState.EXISTING;
	private double existenceTimeCountdown = PRE_FADE_EXISTENCE_TIME;
	
	//----------------------------------------------------------------------------
	// Constructor(s)
	//----------------------------------------------------------------------------
	
	public MessengerRna(IObtainGeneModelElements model, Point2D initialPosition, double initialLength) {
		super(model, createInitialShape(initialLength), initialPosition, ELEMENT_PAINT);
		length = initialLength;
	}
	
	public MessengerRna(IObtainGeneModelElements model, double initialLength) {
		this(model, new Point2D.Double(), initialLength);
	}

	public MessengerRna(IObtainGeneModelElements model) {
		this(model, new Point2D.Double(), DEFAULT_LENGTH);
	}

	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	/**
	 * Grow the RNA strand by the specified length.
	 * 
	 * IMPORTANT NOTE: This routine always adds segments to the right side of
	 * the strand.  This is because this was the only growth pattern needed at
	 * the time of the method's creation (in November of 2009).  It would be
	 * possible to generalize this to grow in any direction, or to expand from
	 * the center.  Feel free to do so if needed.
	 */
	public void grow( double growthAmount ){
		
		if (length == 0){
			assert points.size() == 0; // Shouldn't have any points yet if length is zero.
			points.add(new Point2D.Double(0, 0)); // Add the starting point.
		}
		else if (points.size() == 0){
			// This is the case where the messenger RNA was created with an
			// initial length and then asked to grow.  In this case, we need
			// to deduce the points from the shape the first time through.
			double [] coords = new double[6];
			for (PathIterator it = getShape().getPathIterator(new AffineTransform()); !it.isDone(); it.next()){
				it.currentSegment(coords);
				points.add(new Point2D.Double(coords[0], coords[1]));
			}
			// At this step, we actually have twice as many points as we
			// need, since we have points for the top and bottom of the closed
			// path.  To correct this, we create a new array that has only the
			// top points, correctly compensated.
			assert points.size() % 2 == 0;
			ArrayList<Point2D> adjustedPoints = new ArrayList<Point2D>();
			for (int i = 0; i < points.size() / 2; i++){
				adjustedPoints.add(new Point2D.Double(points.get(i).getX(), points.get(i).getY() - THICKNESS / 2));
			}
			points = adjustedPoints;
		}
		
		// Add a new point to the list.  The new point is always added at a Y
		// offset of 0 so that it looks like it is being produced from the
		// same location, but all the other points are moved up or down a
		// little to make the output look wavy.
		double yVariation = (RAND.nextDouble() - 0.5) * growthAmount;
		for (Point2D point : points){
			point.setLocation(point.getX(), point.getY() + yVariation);
		}
		Point2D currentEndPoint = points.get(points.size() - 1);
		points.add(new Point2D.Double(currentEndPoint.getX() + growthAmount, 0));
		
		// Update the shape.
		setShape(createPathFromPoints(points).getGeneralPath());
		
		// Update the length.
		length += growthAmount;
	}
	
	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		switch (existenceState){
		case EXISTING:
			existenceTimeCountdown -= dt;
			if (existenceTimeCountdown <= 0){
				// Stop moving.
				setMotionStrategy(new StillnessMotionStrategy(this));
				
				// Spawn a process arrow to indicate that we are transforming.
				Rectangle2D bounds = getShape().getBounds2D();
				Point2D processArrowPos = new Point2D.Double(bounds.getCenterX() + getPositionRef().getX(),
						bounds.getMaxY() + getPositionRef().getY() + 4);
				getModel().addTransformationArrow(new TransformationArrow(getModel(), processArrowPos));
				
				// Start fading away.
				existenceState = ExistenceState.FADING_OUT;
			}
			break;
			
		case FADING_OUT:
			if (getExistenceStrength() > 0){
				setExistenceStrength(Math.max(getExistenceStrength() - FADE_RATE, 0));
			}
			// Note: When we get fully faded out, we will be automatically
			// removed from the model.
			break;
			
		default:
			System.err.println(getClass().getName() + " - Error: Unexpected existence state.");
			assert false;
			break;
		}
	}

	@Override
	public ModelElementType getType() {
		return ModelElementType.MESSENGER_RNA;
	}
	
	private static Shape createInitialShape(double length){
		
		if (length < DEFAULT_LENGTH){
			// For a length of essentially zero, return a dot.
			return new Ellipse2D.Double(-THICKNESS / 2, -THICKNESS / 2, THICKNESS, THICKNESS);
		}
		else{
			ArrayList<Point2D> linePoints = new ArrayList<Point2D>();
			for (int i = 0; i < Math.floor(length / DEFAULT_LENGTH); i++){
				// Add a point.
				linePoints.add(new Point2D.Double(-length / 2 + i * DEFAULT_SEGMENT_LENGTH, 0));
			}
			return createPathFromPoints(linePoints).getGeneralPath();
		}
	}
	
	/**
	 * Create a closed shape representing a curve from a set of points.  In
	 * order for the shape to be closed, this draws a top line, then a bottom
	 * line, and connects them.
	 * 
	 * @param points
	 * @return
	 */
	private static DoubleGeneralPath createPathFromPoints(ArrayList<Point2D> points){
		
		DoubleGeneralPath path = new DoubleGeneralPath(points.get(0));
		
		// Draw the top line.
		for (int i = 1; i < points.size(); i++){
			path.lineTo(points.get(i).getX(), points.get(i).getY() + THICKNESS / 2);
		}
		
		// Draw the bottom line.
		for (int i = points.size() - 1; i >= 0; i--){
			path.lineTo(points.get(i).getX(), points.get(i).getY() - THICKNESS / 2);
		}
		
		path.closePath();
		
		return path;
	}
}
