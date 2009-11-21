/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This is a base class for model elements that exist inside a cell or in
 * extracellular space, and that are not composed of any other model elements.
 * They are, in a sense, the atomic elements of this model.
 * 
 * @author John Blanco
 */
public abstract class SimpleModelElement implements IModelElement{
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	// Range within with bonding can occur.
	protected static final double ATTACHMENT_INITIATION_RANGE = 70;  // In nanometers.
	
	// Range at which a bond forms when two binding partners are moving
	// towards each other.
	protected static final double ATTACHMENT_FORMING_DISTANCE = 1; // In nanometers.
	
	// Rate at which elements fade in and out of existence.
	protected static final double FADE_RATE = 0.05;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private Shape shape;
	private Point2D position;
	private Paint paint;  // The paint to use when representing this element in the view.
	private double existenceStrength = 1.0; // Maps to transparency in the view.
	private ExistenceState existenceState = ExistenceState.EXISTING; // Controls whether fading in or out.
	private Vector2D.Double velocity = new Vector2D.Double();
    protected ArrayList<IModelElementListener> listeners = new ArrayList<IModelElementListener>();
    private ArrayList<AttachmentPoint> bindingPoints = new ArrayList<AttachmentPoint>();
    private AbstractMotionStrategy motionStrategy = null;
    private final IObtainGeneModelElements model;
    private boolean dragging;
    private double existenceTimeCountdown;
    private double existenceTime;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
    public SimpleModelElement(IObtainGeneModelElements model, Shape initialShape, Point2D initialPosition,
    		Paint paint, boolean fadeIn, double existenceTime){
		
		this.model = model;
		this.shape = initialShape;
		this.position = initialPosition;
		this.paint = paint;
		this.existenceTime = existenceTime;
		
		if (fadeIn){
			setExistenceState(ExistenceState.FADING_IN);
			setExistenceStrength(0.01);
		}
		else{
			setExistenceState(ExistenceState.EXISTING);
			setExistenceStrength(1);
			existenceTimeCountdown = existenceTime;
		}
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
    public boolean isUserControlled() {
    	return dragging;
    }
    
	public abstract ModelElementType getType();
	
	public boolean isPartOfDnaStrand(){
		// Assumed not to be part of DNA strand in base class, override as needed.
		return false;
	}
	
	public Shape getShape(){
		return shape;
	}
	
	protected IObtainGeneModelElements getModel(){
		return model;
	}
	
	protected void setShape(Shape shape){
		this.shape = shape;
		notifyShapeChanged();
	}
	
	public Point2D getPositionRef(){
		return position;
	}
	
	public void setPosition(double xPos, double yPos ){
		if (xPos != position.getX() || yPos != position.getY()){
			position.setLocation(xPos, yPos);
			notifyPositionChanged();
		}
	}
	
	public void setPosition(Point2D newPosition){
		setPosition(newPosition.getX(), newPosition.getY());
	}
	
	public Paint getPaint(){
		return paint;
	}
	
	public String getLabel(){
		return null;
	}
	
	public void setVelocity(double xVel, double yVel){
		velocity.setComponents(xVel, yVel);
	}
	
	public void setVelocity(Vector2D newVelocity){
		setVelocity(newVelocity.getX(), newVelocity.getY());
	}
	
	public Vector2D getVelocityRef(){
		return velocity;
	}
	
	/**
	 * Update the position of this model element based on its current position
	 * and its velocity.  Note that this assumes that this must be called at
	 * an appropriate frequency in order of the motion of the model element to
	 * be correct.
	 */
	public void updatePosition(){
		if (velocity.getX() != 0 || velocity.getY() != 0 ){
			position.setLocation(position.getX() + velocity.getX(), position.getY() + velocity.getY());
			notifyPositionChanged();
		}
	}
	
	public AttachmentPoint getAttachmentPointForElement(ModelElementType elementType){
		AttachmentPoint matchingBindingPoint = null;
		for (AttachmentPoint bindingPoint : bindingPoints){
			if (bindingPoint.getElementType() == elementType){
				// We have a match.
				matchingBindingPoint = bindingPoint;
				break;
			}
		}
		if (matchingBindingPoint == null){
			System.err.println(getClass().getName() + " - Warning: " + this.getType() + " has no binding point found for " + elementType);
		}
		return matchingBindingPoint;
	}
	
	public double getExistenceStrength() {
		return existenceStrength;
	}
	
	protected void setExistenceStrength(double existenceStrength){
		if (this.existenceStrength != existenceStrength){
			this.existenceStrength = existenceStrength;
			notifyExistenceStrengthChanged();
		}
	}
	
	protected ExistenceState getExistenceState(){
		return existenceState;
	}
	
	private void setExistenceState(ExistenceState existenceState){
		this.existenceState = existenceState;
	}
	
	/**
	 * This element is being removed from the model.  Do any cleanup needed
	 * and send out notifications.
	 */
	public void removeFromModel(){
		notifyRemovedFromModel();
	}

	/**
	 * Add a binding point to the list that is being maintained for this model
	 * element.
	 */
	protected void addAttachmentPoint(AttachmentPoint bindingPoint){
		bindingPoints.add(bindingPoint);
	}
	
	protected void notifyPositionChanged(){
		// Notify all listeners of the position change.
		for (IModelElementListener listener : listeners)
		{
			listener.positionChanged();
		}
	}
	
	private void notifyExistenceStrengthChanged(){
		for (IModelElementListener listener : listeners)
		{
			listener.existenceStrengthChanged();
		}		
	}
	
	private void notifyRemovedFromModel(){
		for (IModelElementListener listener : listeners)
		{
			listener.removedFromModel();
		}		
	}
	
	protected void notifyShapeChanged(){
		// Notify all listeners of the shape change.
		for (IModelElementListener listener : listeners)
		{
			listener.shapeChanged();
		}
	}
	
	public void addListener(IModelElementListener listener) {
		if (listeners.contains( listener ))
		{
			// Don't bother re-adding.
			System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
			assert false;
			return;
		}
		
		listeners.add( listener );
	}
	
	public void removeListener(IModelElementListener listener){
		listeners.remove(listener);
	}
	
	public boolean releaseAttachmentWith(IModelElement modelElement) {
		// Always refuses to release in the base class.
		return false;
	}

	public void stepInTime(double dt) {
		if (motionStrategy != null){
			motionStrategy.doUpdatePositionAndMotion(dt);
		}
		doFadeInOut(dt);
	}
	
	private void doFadeInOut(double dt){
		switch (getExistenceState()){
		case FADING_IN:
			if (getExistenceStrength() < 1){
				setExistenceStrength(Math.min(getExistenceStrength() + FADE_RATE, 1));
			}
			else{
				// Must be fully faded in, so move to next state.
				setExistenceState(ExistenceState.EXISTING);
				existenceTimeCountdown = existenceTime;
				onTransitionToExistingState();
			}
			break;
			
		case EXISTING:
			if (existenceTime != Double.POSITIVE_INFINITY){
				existenceTimeCountdown -= dt;
				if (existenceTimeCountdown <= 0){
					// Time to fade out.
					setExistenceState(ExistenceState.FADING_OUT);
					onTransitionToFadingOutState();
				}
			}
			break;
			
		case FADING_OUT:
			if (getExistenceStrength() > 0){
				setExistenceStrength(Math.max(getExistenceStrength() - FADE_RATE, 0));
			}
			// Note: When we get fully faded out, we will be removed from the model.
			break;
			
		default:
			assert false;
			break;
		}
	}

	protected void setMotionStrategy(AbstractMotionStrategy motionStrategy){
		if (this.motionStrategy != null){
			this.motionStrategy.cleanup();
		}
		this.motionStrategy = motionStrategy;
	}
	
	/**
	 * This is a hook that allows decendent classes to take some sort of
	 * action when transitioning into the EXISTING state.
	 */
	protected void onTransitionToExistingState(){
		// Does nothing in base class.
	}

	/**
	 * This is a hook that allows decendent classes to take some sort of
	 * action when transitioning into the FADING_OUT state.
	 */
	protected void onTransitionToFadingOutState(){
		// Does nothing in base class.
	}
	
	/**
	 * Set the time that this model element will exist after fading in (if it
	 * fades in) and before fading out.  Note that this will only work when
	 * the element is fading in, after that it's too late.
	 * 
	 * @param existenceTime
	 */
	protected void setExistenceTime(double existenceTime){
		if (existenceState != ExistenceState.FADING_IN){
			System.err.println(getClass().getName() + " - Warning: Setting existence time when not fading in, this will have no affect.");
		}
		this.existenceTime = existenceTime;
	}
	
	protected AbstractMotionStrategy getMotionStrategyRef(){
		return motionStrategy;
	}

    public void setDragging(boolean dragging) {
        this.dragging=dragging;
    }
    
    protected enum ExistenceState { FADING_IN, EXISTING, FADING_OUT };
}
