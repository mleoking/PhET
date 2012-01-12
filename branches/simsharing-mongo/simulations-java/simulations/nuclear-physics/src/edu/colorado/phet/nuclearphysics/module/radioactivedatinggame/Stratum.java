// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * This class represents a "stratum" in the model, which means a layer of rock
 * and/or soil with internally consistent characteristics that distinguishes 
 * it from contiguous layers (definition obtained from wikipedia).
 */
public class Stratum {
	
	private static final int NUM_EVAL_POINTS_FOR_DEPTH = 10;
	
    private LayerLine _topLine;
    private LayerLine _bottomLine;
    private GeneralPath _path = new GeneralPath();

    public Stratum( LayerLine topLine, LayerLine bottomLine ){
    	
    	_topLine = topLine;
    	_bottomLine = bottomLine;

    	// Verify that the bottom is lower than the top.
    	if (bottomLine.getMinDepth() > topLine.getMaxDepth()){
    		throw new IllegalArgumentException("No portion of bottom line can be above top line.");
    	}
    	
    	// Create the shape that will represent this stratum.
    	_path.append(topLine, true);
    	_path.append(new Line2D.Double(_topLine.getRightmostPoint(), _bottomLine.getRightmostPoint()), true);
    	_path.append(_bottomLine.getReverseLine(), true);
    	_path.append(new Line2D.Double(_bottomLine.getLeftmostPoint(), _topLine.getLeftmostPoint()), true);
    }
    
    public Shape getShape(){
    	return _path;
    }
    
    public double getBottomOfStratumY() {
    	double bottomDepth = Double.POSITIVE_INFINITY;
    	for (int i = 0; i < NUM_EVAL_POINTS_FOR_DEPTH; i++){
    		double t = (double)i * (1.0 / (double)NUM_EVAL_POINTS_FOR_DEPTH);
    		if (evaluateCurve(_bottomLine, t).getY() < bottomDepth){
    			bottomDepth = evaluateCurve(_bottomLine, t).getY();
    		}
    	}
    	return bottomDepth;
    }

    public double getTopOfStratumY() {
    	double topDepth = Double.NEGATIVE_INFINITY;
    	for (int i = 0; i < NUM_EVAL_POINTS_FOR_DEPTH; i++){
    		double t = (double)i * (1.0 / (double)NUM_EVAL_POINTS_FOR_DEPTH);
    		if (evaluateCurve(_topLine, t).getY() > topDepth){
    			topDepth = evaluateCurve(_topLine, t).getY();
    		}
    	}
    	return topDepth;
    }

    public LayerLine getTopLine() {
        return _topLine;
    }

    public LayerLine getBottomLine() {
        return _bottomLine;
    }
    
    /**
     * Get the y location of the bottom of the stratum at the given horizontal
     * location.
     */
    public double getBottomYGivenX( double xPos ){
    	return getYForGivenX(xPos, _bottomLine);
    }
    
    /**
     * Get the y location of the top of the stratum at the given horizontal
     * location.
     */
    public double getTopYGivenX( double xPos ){
    	return getYForGivenX(xPos, _topLine);
    }
    
    private double getYForGivenX( double xPos, CubicCurve2D layerLine ){
    	// Validate arguments.
    	if (xPos < layerLine.getP1().getX() || xPos > layerLine.getP2().getX()){
    		System.err.println(this.getClass().getName() + ": Warning - given point outside stratum width.");
    		assert false;
    		return 0;
    		
    	}
    	double proportion = (xPos - layerLine.getP1().getX()) / (layerLine.getP2().getX() - layerLine.getP1().getX());
    	
    	return evaluateCurve(layerLine, proportion).getY();
    }
    
    /**
     * Evaluate the curve in order to locate a point given a distance along
     * the curve.  This uses the DeCasteljau algorithm.
     * 
     * @param curve - The CubicCurve2D that is being evaluated.
     * @param t - proportional distance along the curve from the first control point, must be from 0 to 1.
     * @return point corresponding to the location of the curve at the specified distance.
     */
    private Point2D evaluateCurve(CubicCurve2D curve, double t){
        if ( t < 0 || t > 1 ) {
            throw new IllegalArgumentException( "t is out of range: " + t );
        }
        Point2D ab = linearInterpolation(curve.getP1(), curve.getCtrlP1(), t);
        Point2D bc = linearInterpolation(curve.getCtrlP1(), curve.getCtrlP2(), t);
        Point2D cd = linearInterpolation(curve.getCtrlP2(), curve.getP2(), t);
        Point2D abbc = linearInterpolation(ab, bc, t);
        Point2D bccd = linearInterpolation(bc, cd, t);
        
        return linearInterpolation(abbc, bccd, t);

    }
    
    /**
     * Simple linear interpolation between two points.
     */
    private Point2D linearInterpolation(Point2D a, Point2D b, double t){
    	return ( new Point2D.Double( a.getX() + (b.getX() - a.getX()) * t,  a.getY() + (b.getY() - a.getY()) * t));
    }
    
    /**
     * This class is used to define the lines that are used to describe the
     * upper and lower boundaries of a stratum.  It is assumed that these
     * lines are under the ground, which is why the term "depth" is used to
     * describe the vertical position of the line.
     */
    public static class LayerLine extends CubicCurve2D.Double {
    	
    	// Controls the maximum amount of squiggle.  For more squiggle,
    	// increase this value.
    	private static final double MAX_CNTRL_POINT_DISTANCE = 2;
    	
    	// Default width of a layer line.
    	private static final double DEFAULT_LAYER_LINE_WIDTH = 200;  // In meters.
    	
    	// Random number generator.  Seed with a value so that the behavior
    	// is consistent each time the sim is run.
    	private static final Random RAND = new Random(342);
    	
    	private final Point2D _leftmostPoint;
		private final Point2D _rightmostPoint;
		private final Point2D _controlPoint1;
		private final Point2D _controlPoint2;
    	
    	/**
    	 * Create a layer line at the specified depth and add a * bit of
    	 * "squiggle" to the line so that it is not perfectly and boringly
    	 * straight.
    	 * 
    	 * @param width
    	 * @param depth
    	 */
    	public LayerLine(double width, double depth) {
    		_leftmostPoint = new Point2D.Double( -width / 2, -depth );
    		_rightmostPoint = new Point2D.Double( width / 2, -depth );
    		_controlPoint1 = new Point2D.Double( -width / 3, -depth + (RAND.nextDouble() - 0.5) * 2 
    				* MAX_CNTRL_POINT_DISTANCE );
    		// Thing get a little too skewed if both control points are above
    		// or below, so make sure we have one of each.
    		if (_controlPoint1.getY() > -depth){
	    		_controlPoint2 = new Point2D.Double( width / 3, 
	    				-depth - RAND.nextDouble() * MAX_CNTRL_POINT_DISTANCE );
    		}
    		else{
	    		_controlPoint2 = new Point2D.Double( width / 3, 
	    				-depth + RAND.nextDouble() * MAX_CNTRL_POINT_DISTANCE );
    		}
    		
    		setCurve(_leftmostPoint, _controlPoint1, _controlPoint2, _rightmostPoint);
    	}
    	
    	public LayerLine( double depth ){
    		this(DEFAULT_LAYER_LINE_WIDTH, depth);
    	}
    	
    	public double getMaxDepth(){
    		return getBounds2D().getMinY();
    	}

    	public double getMinDepth(){
    		return getBounds2D().getMaxY();
    	}
    	
    	public Point2D getLeftmostPoint() {
			return _leftmostPoint;
		}

		public Point2D getRightmostPoint() {
			return _rightmostPoint;
		}
		
		/**
		 * Get a reversed version of the line that represents this layer line.
		 * This is useful when creating 2D shapes from these layer lines.
		 * @return
		 */
		public Shape getReverseLine() {
			CubicCurve2D reversedLine = new CubicCurve2D.Double();
			reversedLine.setCurve(_rightmostPoint, _controlPoint2, _controlPoint1, _leftmostPoint);
			return reversedLine;
		}
    }
}
