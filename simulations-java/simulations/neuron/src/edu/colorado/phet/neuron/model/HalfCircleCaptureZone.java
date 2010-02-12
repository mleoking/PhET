package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

public class HalfCircleCaptureZone extends AbstractCaptureZone {

	private Arc2D halfCircleShape;
	private Point2D center;
	private double radius;
	private double angle;
	
	/**
	 * Constructor - defines the size and orientation of the half circle shape
	 * the constitutes this capture zone.
	 * 
	 * @param radius - in nanometers (which is the unit used in the model).
	 * @param angle - in radians, 0 signifies half circle with the right side showing.
	 */
	public HalfCircleCaptureZone(Point2D center, double radius, double angle){
		this.center = center;
		this.radius = radius;
		this.angle = angle;
		updateShape();
	}
	
	@Override
	public Shape getShape() {
		return halfCircleShape;
	}

	@Override
	public boolean isPointInZone(Point2D pt) {
		return halfCircleShape.contains(pt);
	}
	
	@Override
	public void setRotationalAngle(double angle) {
		this.angle = angle;
		updateShape();
	}
	
	@Override
	public void setCenterPoint(Point2D center) {
		this.center = center;
		updateShape();
	}
	
	private void updateShape(){
		halfCircleShape = new Arc2D.Double();
//		halfCircleShape.setArcByCenter(center.getX(), center.getY(), radius, Math.toDegrees(angle)-180, 180, Arc2D.CHORD);
		halfCircleShape.setArcByCenter(center.getX(), center.getY(), radius, -Math.toDegrees(angle)-90, 180, Arc2D.CHORD);
	}
}
