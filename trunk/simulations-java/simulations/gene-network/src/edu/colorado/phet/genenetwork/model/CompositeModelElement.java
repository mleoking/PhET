package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.Dimension2D;
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
	private ArrayList<SimpleModelElement> constituentModelElements = new ArrayList<SimpleModelElement>();
	
	/**
	 * This constructor assumes that the simple model elements need to be
	 * moved such that their binding sites all coincide.
	 * 
	 * @param simpleModelElements
	 */
	public CompositeModelElement(ArrayList<SimpleModelElement> simpleModelElements, Point2D initialPosition){

		if (simpleModelElements.size() < 2){
			throw new IllegalArgumentException("Insufficent number of elements, much be at least 2");
		}
		
		constituentModelElements.addAll(simpleModelElements);
		
		// Position each simple model element such that the binding
		// points are all at 0,0.
		for (int i = 0; i < constituentModelElements.size(); i++){
			SimpleModelElement modelElement = constituentModelElements.get(i);
			SimpleModelElement bondingToModelElement =
				constituentModelElements.get((i + 1) % constituentModelElements.size());
    		Dimension2D bindingPointOffset =
    			modelElement.getBindingPointForElement(bondingToModelElement.getType()).getOffset();
    		modelElement.setPosition(-bindingPointOffset.getWidth(), -bindingPointOffset.getHeight());
		}
		
		setPosition(initialPosition);
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
		
		for (SimpleModelElement modelElement : constituentModelElements){
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
		for (SimpleModelElement modelElement : constituentModelElements){
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
	
	public ArrayList<SimpleModelElement> getConstituents(){
		return new ArrayList<SimpleModelElement>(constituentModelElements);
	}

	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean availableForBonding(SimpleElementType elementType) {
		// Always says no in the base class.
		return false;
	}

	public boolean considerProposal(IModelElement modelElement) {
		// Always refuses proposal in the base class.
		return false;
	}

	public boolean releaseBondWith(IModelElement modelElement) {
		// Always refuses to release in the base class.
		return false;
	}

	public void updatePositionAndMotion() {
		// Does nothing in the base class, which essentially means it never
		// moves.
	}

	public void updatePotentialBondingPartners( ArrayList<IModelElement> modelElements ) {
		// Does nothing in the base class, which essentially means that it
		// if not overridden it will not initiate any bonds.
	}
}