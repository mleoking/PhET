/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.graphics.ReversePathIterator;


/**
 * Model representation for the axon membrane.  Represents it as a cross
 * section and a shape that is intended to look like the body of the axon
 * receding into the distance.
 * 
 * @author John Blanco
 */
public class AxonMembrane {

	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	// For now, and unless there is some reason to do otherwise, the center
	// of the cross section is positioned at the origin.
	private static final Point2D CROSS_SECTION_CENTER = new Point2D.Double(0, 0);

	// Fixed membrane characteristics.
	public static final double MEMBRANE_THICKNESS = 4;  // In nanometers, obtained from web research.
	private static final double DEFAULT_DIAMETER = 80; // In nanometers.
	private static final double BODY_LENGTH = DEFAULT_DIAMETER * 1.5;
	private static final double BODY_TILT_ANGLE = Math.PI/4;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ArrayList<Listener> listeners = new ArrayList<Listener>();

	// Shape of the cross section of the membrane.
	private Ellipse2D crossSectionEllipseShape = new Ellipse2D.Double(-DEFAULT_DIAMETER / 2, -DEFAULT_DIAMETER / 2,
			DEFAULT_DIAMETER, DEFAULT_DIAMETER);
	
	// Shape of the body of the axon.
	private Shape bodyShape;
	
	// Traveling action potential that moves down the membrane.
	private TravelingActionPotential travelingActionPotential;

	// Points that define the body shape.
	private Point2D vanishingPoint;

	private Point2D intersectionPointA;

	private Point2D intersectionPointB;

	private Point2D cntrlPtA1;

	private Point2D cntrlPtA2;

	private Point2D cntrlPtB1;

	private Point2D cntrlPtB2;

	private CubicCurve2D curveA;

	private CubicCurve2D curveB;
	
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
	
	public AxonMembrane() {
		bodyShape = createAxonBodyShape();
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	public Ellipse2D getCrossSectionEllipseShape() {
		return crossSectionEllipseShape;
	}

	public void setCrossSectionEllipseShape(Ellipse2D crossSectionEllipseShape) {
		this.crossSectionEllipseShape = crossSectionEllipseShape;
	}
	
	public double getMembraneThickness(){
		return MEMBRANE_THICKNESS;
	}
	
	public double getCrossSectionDiameter(){
		return DEFAULT_DIAMETER;
	}
	
	public Shape getAxonBodyShape(){
		return bodyShape;
	}
	
	public CubicCurve2D getCurveA(){
		return curveA;
	}
	
	public CubicCurve2D getCurveB(){
		return curveB;
	}
	
    /**
     * Create the shape of the axon body based on the size and position of the
     * cross section and some other fixed parameters.  This is a 2D shape that
     * is intended to look like a receding 3D shape when presented in the
     * view, so its creation is a little unusual.
     */
    private Shape createAxonBodyShape(){
    	
    	GeneralPath axonBodyShape = new GeneralPath();

    	vanishingPoint = new Point2D.Double(BODY_LENGTH * Math.cos(BODY_TILT_ANGLE),
    			BODY_LENGTH * Math.sin(BODY_TILT_ANGLE));
    	
    	// Find the two points at which the shape will intersect the outer
    	// edge of the cross section.
    	double r = getCrossSectionDiameter() / 2 + getMembraneThickness() / 2;
    	double theta = BODY_TILT_ANGLE + Math.toRadians(90);
    	intersectionPointA = new Point2D.Double(r * Math.cos(theta), r * Math.sin(theta));
    	theta += Math.PI;
    	intersectionPointB = new Point2D.Double(r * Math.cos(theta), r * Math.sin(theta));
    	
    	// Define the control points for the two curves.  Note that there is
    	// some tweaking in here, so change as needed to get the desired look.
    	// If you can figure it out, that is.
    	double ctrlPtRadius;
    	double angleToVanishingPt = Math.atan2(vanishingPoint.getY() - intersectionPointA.getY(), 
    			vanishingPoint.getX() - intersectionPointA.getX());
    	ctrlPtRadius = intersectionPointA.distance(vanishingPoint) * 0.33;
    	cntrlPtA1 = new Point2D.Double(
    			intersectionPointA.getX() + ctrlPtRadius * Math.cos(angleToVanishingPt + 0.3), 
    			intersectionPointA.getY() + ctrlPtRadius * Math.sin(angleToVanishingPt + 0.3));
    	ctrlPtRadius = intersectionPointA.distance(vanishingPoint) * 0.67;
    	cntrlPtA2 = new Point2D.Double( 
    			intersectionPointA.getX() + ctrlPtRadius * Math.cos(angleToVanishingPt - 0.5), 
    			intersectionPointA.getY() + ctrlPtRadius * Math.sin(angleToVanishingPt - 0.5));
    	
    	double angleToIntersectionPt = Math.atan2(intersectionPointB.getY() - vanishingPoint.getY(), 
    			intersectionPointB.getX() - intersectionPointB.getX());
    	ctrlPtRadius = intersectionPointB.distance(vanishingPoint) * 0.33;
    	cntrlPtB1 = new Point2D.Double(
    			vanishingPoint.getX() + ctrlPtRadius * Math.cos(angleToIntersectionPt + 0.1), 
    			vanishingPoint.getY() + ctrlPtRadius * Math.sin(angleToIntersectionPt + 0.1));
    	ctrlPtRadius = intersectionPointB.distance(vanishingPoint) * 0.67;
    	cntrlPtB2 = new Point2D.Double( 
    			vanishingPoint.getX() + ctrlPtRadius * Math.cos(angleToIntersectionPt - 0.25), 
    			vanishingPoint.getY() + ctrlPtRadius * Math.sin(angleToIntersectionPt - 0.25));
    	
    	// Create the curves that define the boundaries of the body.
    	curveA = new CubicCurve2D.Double(vanishingPoint.getX(), vanishingPoint.getY(), cntrlPtA2.getX(),
    			cntrlPtA2.getY(), cntrlPtA1.getX(), cntrlPtA1.getY(), intersectionPointA.getX(),
    			intersectionPointA.getY());
    	curveB = new CubicCurve2D.Double(vanishingPoint.getX(), vanishingPoint.getY(), cntrlPtB1.getX(),
    			cntrlPtB1.getY(), cntrlPtB2.getX(), cntrlPtB2.getY(), intersectionPointB.getX(),
    			intersectionPointB.getY());
    	
    	// Set up the shape of the neuron body.
    	axonBodyShape.append(ReversePathIterator.getReversePathIterator(curveA), false);
    	axonBodyShape.append(curveB, false);
    	axonBodyShape.lineTo(intersectionPointA.getX(), intersectionPointA.getY());
    	
    	return axonBodyShape;
    }
    
    /**
     * Evaluate the curve in order to locate a point given a distance along
     * the curve.  This uses the DeCasteljau algorithm.
     * 
     * @param curve - The CubicCurve2D that is being evaluated.
     * @param t - proportional distance along the curve from the first control point, must be from 0 to 1.
     * @return point corresponding to the location of the curve at the specified distance.
     */
    private static Point2D evaluateCurve(CubicCurve2D curve, double t){
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
    private static Point2D linearInterpolation(Point2D a, Point2D b, double t){
    	return ( new Point2D.Double( a.getX() + (b.getX() - a.getX()) * t,  a.getY() + (b.getY() - a.getY()) * t));
    }
    
    /**
     * Start an action potential that will travel down the length of the
     * membrane toward the transverse cross section.
     */
    public void initiateTravelingActionPotential(){
    	travelingActionPotential = new TravelingActionPotential(this);
    	travelingActionPotential.addListener(new TravelingActionPotential.Adapter(){
			public void travelingCompleted() {
				notifyTravelingActionPotentialEnded();
				travelingActionPotential = null;
			}
    	});
    	notifyTravelingActionPotentialStarted();
    }
    
    /**
     * Get the object that defines the current traveling action potential.
     * Returns null if no action potential is happening.
     */
    public TravelingActionPotential getTravelingActionPotential(){
    	return travelingActionPotential;
    }
    
    /**
     * Step this model element forward in time by the specified delta.
     * 
     * @param dt - delta time, in seconds.
     */
    public void stepInTime(double dt){
    	if (travelingActionPotential != null){
    		travelingActionPotential.stepInTime(dt);
    	}
    }
    
	public void addListener(Listener listener){
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(listener);
	}
	
	private void notifyTravelingActionPotentialStarted(){
		for (Listener listener : listeners){
			listener.travelingActionPotentialStarted();
		}
	}
    
	private void notifyTravelingActionPotentialEnded(){
		for (Listener listener : listeners){
			listener.travelingActionPotentialEnded();
		}
	}
    
    /**
     * Interface for listening to notifications from the axon membrane.
     *
     */
    public interface Listener {
    	void travelingActionPotentialStarted();
    	void travelingActionPotentialEnded();
    }
    
    public static class Adapter implements Listener {
		public void travelingActionPotentialEnded() {}
		public void travelingActionPotentialStarted() {}
    }
    
    /**
     * Class the defines the behavior of the action potential that travels
     * along the membrane before reaching the location of the transverse cross
     * section.  This is essentially just a shape that is intended to look
     * like something moving along the outer membrane.
     */
    public static class TravelingActionPotential {
    	
    	private static double LIFETIME = 0.0015; // In seconds of sim time (not wall time).
    	
        private ArrayList<Listener> listeners = new ArrayList<Listener>();
    	private double lifetimeCountdownTimer = LIFETIME;
    	private Shape shape;
    	private AxonMembrane axonMembrane;
    	
    	public TravelingActionPotential(AxonMembrane axonMembrane){
    		this.axonMembrane = axonMembrane;
    		updateShape();
    	}
    	
    	/**
    	 * Step this model component forward by the specified time.  This will
    	 * update the shape such that it will appear to move down the axon
    	 * membrane.
    	 * 
    	 * @param dt
    	 */
    	public void stepInTime(double dt){
    		if (lifetimeCountdownTimer > 0){
    			lifetimeCountdownTimer -= dt;
    			if (lifetimeCountdownTimer <= 0){
    				shape = null;
    				notifyTravelingCompleted();
    			}
    			else{
    				updateShape();
    				notifyShapeChanged();
    			}
    		}
    	}
    	
    	/**
    	 * Update the shape as a function of the current value of the lifetime
    	 * counter.
    	 * 
    	 * NOTE: An attempt was made to generalize this so that it would work
    	 * for pretty much any shape of the axon body, but this turned out to
    	 * be a lot of work.  As a result, if significant changes are made to
    	 * the axon body shape, this routine will need to be updated.
    	 */
    	private void updateShape(){
			Point2D startPoint = AxonMembrane.evaluateCurve(axonMembrane.getCurveA(), 1 - lifetimeCountdownTimer / LIFETIME);
			Point2D endPoint = AxonMembrane.evaluateCurve(axonMembrane.getCurveB(), 1 - lifetimeCountdownTimer / LIFETIME);
			Point2D midPoint = new Point2D.Double((startPoint.getX() + endPoint.getX()) / 2, (startPoint.getY() + endPoint.getY()) / 2);
			// The exponents used in the control point distances can be
			// adjusted to make the top or bottom more or less curved as the
			// potential moves down the membrane.
			double ctrlPoint1Distance = endPoint.distance(startPoint) * 0.7 * Math.pow((1 - lifetimeCountdownTimer / LIFETIME), 1.8);
			double ctrlPoint2Distance = endPoint.distance(startPoint) * 0.7 * Math.pow((1 - lifetimeCountdownTimer / LIFETIME), 0.8);
			double perpendicularAngle = Math.atan2(endPoint.getY() - startPoint.getY(), endPoint.getX() - startPoint.getX()) + Math.PI / 2;
			Point2D ctrlPoint1 = new Point2D.Double(
					midPoint.getX() + ctrlPoint1Distance * Math.cos(perpendicularAngle + Math.PI / 6), 
					midPoint.getY() + ctrlPoint1Distance * Math.sin(perpendicularAngle + Math.PI / 6));
			Point2D ctrlPoint2 = new Point2D.Double(
					midPoint.getX() + ctrlPoint2Distance * Math.cos(perpendicularAngle - Math.PI / 6), 
					midPoint.getY() + ctrlPoint2Distance * Math.sin(perpendicularAngle - Math.PI / 6));
			shape = new CubicCurve2D.Double(startPoint.getX(), startPoint.getY(), ctrlPoint1.getX(), ctrlPoint1.getY(), ctrlPoint2.getX(), ctrlPoint2.getY(), endPoint.getX(), endPoint.getY());
    	}
    	
		public Shape getShape(){
    		return shape;
    	}
    	
    	public void addListener(Listener listener){
    		listeners.add(listener);
    	}
    	
    	public void removeListener(Listener listener){
    		listeners.remove(listener);
    	}
    	
    	private void notifyTravelingCompleted(){
    		for (Listener listener : listeners){
    			listener.travelingCompleted();
    		}
    	}
        
    	private void notifyShapeChanged(){
    		for (Listener listener : listeners){
    			listener.shapeChanged();
    		}
    	}
        
    	public interface Listener {
    		
    		/**
    		 * Notify the listener that the shape of this model element has
    		 * changed.
    		 */
    		void shapeChanged();
    		
    		/**
    		 * Notify the listener that this has finished traveling down the
    		 * membrane and has arrived at the destination.
    		 */
    		void travelingCompleted();
    	}
    	
    	public static class Adapter implements Listener {
			public void shapeChanged() {}
			public void travelingCompleted() {}
    	}
    }
}
