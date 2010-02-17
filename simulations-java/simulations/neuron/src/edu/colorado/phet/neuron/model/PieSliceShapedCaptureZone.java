/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * A "capture zone" (which is a 2D space that defines where particles may be
 * captured by a gate) that is shaped like a pie slice.
 * 
 * @author John Blanco
 */
public class PieSliceShapedCaptureZone extends CaptureZone {
	
	private static final Random RAND = new Random();

	private Arc2D zoneShape;
	private Point2D originPoint;
	private double radius;
	private double rotationAngle;
	private final double angleOfExtent;
	private final double fixedRotationalOffset;
	
	/**
	 * Constructor - defines the size and orientation of a capture zone which
	 * is shaped like a pie slice.  For more information on what exactly a
	 * capture zone is, see the parent class javadoc.
	 * @param radius - in nanometers
	 * @param fixedRotationalOffset TODO
	 * @param initialRotationAngle - in radians, 0 signifies the point on the left
	 * and the rounded part on the right.
	 * @param angleOfExtent - in radians, extent of the arc.  A value of PI
	 * would be a half circle, PI/2 is a quarter circle.
	 */
	public PieSliceShapedCaptureZone(Point2D center, double radius, double fixedRotationalOffset, 
			double initialRotationAngle, double angleOfExtent){
		
		this.originPoint = center;
		this.radius = radius;
		this.fixedRotationalOffset = fixedRotationalOffset;
		this.rotationAngle = initialRotationAngle;
		this.angleOfExtent = angleOfExtent;
		zoneShape = new Arc2D.Double();
		updateShape();
	}
	
	@Override
	public Shape getShape() {
		return zoneShape;
	}

	@Override
	public boolean isPointInZone(Point2D pt) {
		return zoneShape.contains(pt);
	}
	
	@Override
	public void setRotationalAngle(double angle) {
		this.rotationAngle = angle;
		updateShape();
	}
	
	@Override
	public void setOriginPoint(Point2D center) {
		this.originPoint = center;
		updateShape();
	}
	
	@Override
	public Point2D getOriginPoint() {
		return originPoint;
	}
	
	private void updateShape(){
		zoneShape.setArcByCenter(originPoint.getX(), originPoint.getY(), radius, 
				-Math.toDegrees(fixedRotationalOffset + rotationAngle + angleOfExtent/2),
				Math.toDegrees(angleOfExtent), Arc2D.PIE);
	}

	@Override
	public Point2D getSuggestedNewParticleLocation() {
		// Suggest a random point that is somewhere along the outer curved
		// edge of the pie slice.
		double placementAngle = rotationAngle + fixedRotationalOffset + (RAND.nextDouble() - 0.5) * angleOfExtent;
		double xPos = originPoint.getX() + radius * Math.cos(placementAngle);
		double yPos = originPoint.getY() + radius * Math.sin(placementAngle);
		return new Point2D.Double(xPos, yPos);
	}
}
