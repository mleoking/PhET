package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Class that defines model elements that are composed of a collection of
 * other model elements, either simple or complex.
 */
public class CompositeModelElement implements IModelElement {
	
	private Point2D position = new Point2D.Double();
	private Vector2D velocity = new Vector2D.Double();
	private ArrayList<IModelElement> constituentModelElements = new ArrayList<IModelElement>();
    private AbstractMotionStrategy motionStrategy = null;
	
	protected void addModelElement( IModelElement modelElement ){
		constituentModelElements.add(modelElement);
	}
	
	/**
	 * Set the position for this composite model element, which will in
	 * turn set the position of each constituent element.
	 * 
	 * @param newPosition
	 */
	public void setPosition(double xPos, double yPos){
		
		double deltaX = xPos - position.getX();
		double deltaY = yPos - position.getY();
		
		for (IModelElement modelElement : constituentModelElements){
			modelElement.setPosition(modelElement.getPositionRef().getX() + deltaX,
					modelElement.getPositionRef().getY() + deltaY);
		}
		
		position.setLocation(xPos, yPos);
	}
	
	public Point2D getPositionRef(){
		return position;
	}
	
	public void setPosition(Point2D newPosition){
		setPosition(newPosition.getX(), newPosition.getY());
	}
	
	public void setVelocity(double xVel, double yVel){
		velocity.setComponents(xVel, yVel);
		for (IModelElement modelElement : constituentModelElements){
			modelElement.setVelocity(xVel, yVel);
		}
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
		if (velocity.getX() != 0 || velocity.getY() != 0){
			setPosition(position.getX() + velocity.getX(), position.getY() + velocity.getY());
		}
	}
	
	/**
	 * Get a list containing all the simple model elements contained by this
	 * composite.
	 * 
	 * @return
	 */
	public ArrayList<SimpleModelElement> getSimpleElementConstituents(){
		ArrayList<SimpleModelElement> simpleConstituents = new ArrayList<SimpleModelElement>();
		for (IModelElement modelElement : constituentModelElements){
			if (modelElement instanceof SimpleModelElement){
				simpleConstituents.add((SimpleModelElement)modelElement);
			}
			else {
				simpleConstituents.addAll(getSimpleElementConstituents());
			}
		}
		return simpleConstituents;
	}

	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean availableForBonding(ModelElementType elementType) {
		// Always says no in the base class.
		return false;
	}

	public boolean considerProposalFrom(IModelElement modelElement) {
		// Always refuses proposal in the base class.
		return false;
	}

	public boolean releaseBondWith(IModelElement modelElement) {
		// Always refuses to release in the base class.
		return false;
	}

	public void stepInTime(double dt) {
		if (motionStrategy != null){
			motionStrategy.updatePositionAndMotion(dt);
		}
	}

	public void updatePotentialBondingPartners( ArrayList<IModelElement> modelElements ) {
		// Does nothing in the base class, which essentially means that it
		// if not overridden it will not initiate any bonds.
	}

	public BindingPoint getBindingPointForElement(ModelElementType type) {
		// Return null, indicating that no binding point exists for the specified type.
		return null;
	}

	public ModelElementType getType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void setMotionStrategy(AbstractMotionStrategy newMotionStrategy){
		motionStrategy = newMotionStrategy;
	}

	public void addListener(IModelElementListener listener) {
		// TODO Auto-generated method stub
	}

	public void removeListener(IModelElementListener listener) {
		// TODO Auto-generated method stub
	}
}