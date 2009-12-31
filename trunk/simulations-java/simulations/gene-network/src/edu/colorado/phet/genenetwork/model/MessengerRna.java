/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Class that represents messenger RNA in the model.
 * 
 * NOTE: For the purposes of this sim, it is assumed that the messenger RNA is
 * always fairly horizontal in the model, and never vertical.
 * 
 * @author John Blanco
 */
public abstract class MessengerRna extends SimpleModelElement {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	// Define the appearance.
	private static final Paint ELEMENT_PAINT = Color.BLACK;
	private static float THICKNESS = 0.5f;  // In nanometers.
	
	// Default length of the segments used to create the initial shape.
	private static float GROWTH_SEGMENT_LENGTH = 3;

	// Used so that every strand looks a little different.
	private static final Random RAND = new Random();
	
	// Constants that control when spawning occurs.
	private static final double TIME_TO_FIRST_SPAWNING = 7; // In seconds.
	private static final double TIME_BETWEEN_SPAWNINGS = 2; // In seconds.
	
	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------
	
	private double length = 0;
	private ArrayList<Point2D> points = new ArrayList<Point2D>();
	private boolean fullyFormed = false;
	
	// Counter that tracks the number of "offspring" left to be spawned. 
	private int spawnCounter;
	
	// Count down timer for the next spawning event.
	private double spawningCountdownTimer = TIME_TO_FIRST_SPAWNING;
	
	//----------------------------------------------------------------------------
	// Constructor(s)
	//----------------------------------------------------------------------------
	
	public MessengerRna(IGeneNetworkModelControl model, Point2D initialPosition, double initialLength, int spawnCount, boolean fadeIn) {
		super(model, createInitialShape(), initialPosition, ELEMENT_PAINT, fadeIn, Double.POSITIVE_INFINITY);
		this.spawnCounter = spawnCount;
		while (length < initialLength){
			grow(GROWTH_SEGMENT_LENGTH);
		}
	}
	
	public MessengerRna(IGeneNetworkModelControl model, double initialLength, int spawnCount, boolean fadeIn) {
		this(model, new Point2D.Double(0, 0), initialLength, spawnCount, fadeIn);
	}

	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	/**
	 * Method that is called when it is time to spawn.  Must be overridden in
	 * descendent classes.
	 */
	abstract protected void spawn();
	
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
		// little to make the output look wavy.  The waviness is biased a bit
		// to make it grow in the up direction.
		double yVariation = 1.5 * (RAND.nextDouble() - 0.3) * growthAmount;
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
	
	/**
	 * Set whether this mRNA strand is "fully formed", meaning that it is
	 * capable of producing its target macromolecule.
	 * 
	 * @param fullyFormed
	 */
	public void setFullyFormed(boolean fullyFormed){
		this.fullyFormed = fullyFormed;
	}
	
	public boolean isFullyFormed(){
		return fullyFormed;
	}
	
	/**
	 * This method is used to create a shape that is the same every time and
	 * that can thus be used in places like legends, keys, tool bars, etc.  It
	 * was created for this purpose (i.e. putting mRNA on a legend panel) but
	 * may end up having other uses.
	 */
	public void setPredictibleShape(){
		points = new ArrayList<Point2D>();
		double targetLength = length;
		length = 0;
		RAND.setSeed(3);  // This is what makes it predictable - setting the seed to a fixed value.
		while (length < targetLength){
			grow(GROWTH_SEGMENT_LENGTH);
		}
		
		// Reseed the random number generator with the system clock time in
		// order to make subsequently generated strings look random.
		RAND.setSeed(System.currentTimeMillis());
	}
	
	@Override
	public void stepInTime(double dt) {
		
		if (spawningCountdownTimer != Double.POSITIVE_INFINITY){
	
			spawningCountdownTimer -= dt;
			
			// Time to spawn?
			if (spawningCountdownTimer <= 0){
				if (!isFullyFormed()){
					// This mRNA was not fully formed, so it can't spawn.  Just
					// start fading out.
					setExistenceTime(0);
					spawningCountdownTimer = Double.POSITIVE_INFINITY;
				}
				else{
					if (spawnCounter > 0){
						// Time to spawn a new whatever.
						spawn();
						spawnCounter--;
					}
					if (spawnCounter > 0){
						// Set time until next spawning.
						spawningCountdownTimer = TIME_BETWEEN_SPAWNINGS;
					}
					else{
						// We've done all the spawning that we need to, so it's
						// time to start fading away.
						setExistenceTime(0);
						spawningCountdownTimer = Double.POSITIVE_INFINITY;
					}
				}
			}
		}
		
		super.stepInTime(dt);
	}

	private static Shape createInitialShape(){
		return new Ellipse2D.Double(-THICKNESS / 2, -THICKNESS / 2, THICKNESS, THICKNESS);
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
