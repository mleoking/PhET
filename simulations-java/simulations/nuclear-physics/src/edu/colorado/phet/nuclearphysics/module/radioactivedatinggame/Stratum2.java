/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
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
public class Stratum2 {
    private double bottomOfStratumY;
    private LayerLine _topLine;
    private LayerLine _bottomLine;
    private GeneralPath _path = new GeneralPath();

    public Stratum2( LayerLine topLine, LayerLine bottomLine ){
    	
    	_topLine = topLine;
    	_bottomLine = bottomLine;

    	// Verify that the bottom is lower than the top.
    	if (bottomLine.getMinDepth() > topLine.getMaxDepth()){
    		throw new IllegalArgumentException("No portion of bottom line can be above top line.");
    	}
    	
    	this.bottomOfStratumY = bottomLine.getMaxDepth();
    	
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
        return bottomOfStratumY;
    }

    public LayerLine getTopLine() {
        return _topLine;
    }

    public LayerLine getBottomLine() {
        return _bottomLine;
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
    	private static final double MAX_CNTRL_POINT_DISTANCE = 3;
    	
    	// Default width of a layer line.
    	private static final double DEFAULT_LAYER_LINE_WIDTH = 100;  // In meters.
    	
    	// Random number generator.  Seed with a value so that the behavior
    	// is consistent each time the sim is run.
    	private static final Random RAND = new Random(1234);
    	
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
    		_controlPoint2 = new Point2D.Double( width / 3, -depth + (RAND.nextDouble() - 0.5) * 2 
    				* MAX_CNTRL_POINT_DISTANCE );
    		
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
