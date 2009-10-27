/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * Primary model for the Lac Operon flavor of this sim.
 */
public class LacOperonModel {
    
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	private static final double MODEL_AREA_WIDTH = 100;
	private static final double MODEL_AREA_HEIGHT = 100;
	private static final Rectangle2D MODEL_BOUNDS = new Rectangle2D.Double(-MODEL_AREA_WIDTH / 2,
			-MODEL_AREA_HEIGHT / 2, MODEL_AREA_WIDTH, MODEL_AREA_HEIGHT);
	private static final Random RAND = new Random();
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final GeneNetworkClock clock;
    private ArrayList<SimpleModelElement> simpleModelElements = new ArrayList<SimpleModelElement>();
    private ArrayList<CompositeModelElement> compositeModelElements = new ArrayList<CompositeModelElement>();
    protected ArrayList<Listener> listeners = new ArrayList<Listener>();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LacOperonModel( GeneNetworkClock clock ) {
        super();
        
        this.clock = clock;        
        
        clock.addClockListener(new ClockAdapter(){

			@Override
			public void clockTicked(ClockEvent clockEvent) {
				handleClockTicked();
			}
        	
        });
        
        addInitialModelElements();
    }

	private void addInitialModelElements() {
		
        SimpleModelElement modelElement;
        
        for (int i = 0; i<4; i++){
        	modelElement = new LacZ();
        	randomlyInitModelElement(modelElement);
        	addSimpleModelElement(modelElement);
        }
        for (int i = 0; i<4; i++){
        	modelElement = new LacI();
        	randomlyInitModelElement(modelElement);
        	addSimpleModelElement(modelElement);
        }
        for (int i = 0; i<8; i++){
        	modelElement = new Glucose();
        	randomlyInitModelElement(modelElement);
        	addSimpleModelElement(modelElement);
        }
        for (int i = 0; i<8; i++){
        	modelElement = new Galactose();
        	randomlyInitModelElement(modelElement);
        	addSimpleModelElement(modelElement);
        }
        
        modelElement = new LacPromoter();
        randomlyInitModelElement(modelElement);
        addSimpleModelElement(modelElement);
        
        modelElement = new CapBindingRegion();
        randomlyInitModelElement(modelElement);
        addSimpleModelElement(modelElement);
        
        modelElement = new Cap();
        randomlyInitModelElement(modelElement);
        addSimpleModelElement(modelElement);

        modelElement = new RnaPolymerase();
        randomlyInitModelElement(modelElement);
        addSimpleModelElement(modelElement);

        modelElement = new MessengerRna();
        randomlyInitModelElement(modelElement);
        addSimpleModelElement(modelElement);
        
        // Add composite elements.
        
        // Lactose.
        ArrayList<SimpleModelElement> compositeList = new ArrayList<SimpleModelElement>();
        compositeList.add(new Galactose());
        compositeList.add(new Glucose());
        CompositeModelElement compositeModelElement = new CompositeModelElement(compositeList, new Point2D.Double(0, 0));
        compositeModelElement.setVelocity(1, 0.75);
        addCompositeModelElement(compositeModelElement);
        
        // Lactose bound to LacZ.
        compositeList.clear();
        compositeList.add(new Galactose());
        compositeList.add(new Glucose());
        compositeList.add(new LacZ());
        compositeModelElement = new CompositeModelElement(compositeList, new Point2D.Double(0, 0));
        compositeModelElement.setVelocity(-1, 0.75);
        addCompositeModelElement(compositeModelElement);
	}

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GeneNetworkClock getClock() {
        return clock;
    }

    //----------------------------------------------------------------------------
    // Other Methods
    //----------------------------------------------------------------------------
    
    private void addSimpleModelElement(SimpleModelElement modelElement){
    	simpleModelElements.add(modelElement);
    }
    
    private void addCompositeModelElement(CompositeModelElement compositeModelElement){
    	compositeModelElements.add(compositeModelElement);
    }
    
    private void randomlyInitModelElement(SimpleModelElement modelElement){
    	modelElement.setPosition((RAND.nextDouble() - 0.5) * (MODEL_AREA_WIDTH / 2), 
    			(RAND.nextDouble() - 0.5) * (MODEL_AREA_HEIGHT / 2));
    	double maxVel = 2;
    	modelElement.setVelocity((RAND.nextDouble() - 0.5) * maxVel, (RAND.nextDouble() - 0.5) * maxVel);
    }
    
    /**
     * Get a list of all simple model elements in the model, including any
     * that are currently bound up in composite model elements.
     * @return
     */
    public ArrayList<SimpleModelElement> getAllSimpleModelElements(){
    	ArrayList<SimpleModelElement> allSimples = new ArrayList<SimpleModelElement>(simpleModelElements);
    	for (CompositeModelElement compositeElement : compositeModelElements){
    		allSimples.addAll(compositeElement.getConstituents());
    	}
    	return allSimples;
    }
    
    private void handleClockTicked(){
    	// Update the position of each of the simple model elements.
    	for (SimpleModelElement modelElement : simpleModelElements){
    		
    		Point2D position = modelElement.getPositionRef();
    		Vector2D velocity = modelElement.getVelocityRef();
    		
    		if ((position.getX() > MODEL_BOUNDS.getMaxX() && velocity.getX() > 0) ||
    			(position.getX() < MODEL_BOUNDS.getMinX() && velocity.getX() < 0))	{
    			// Reverse direction in the X direction.
    			modelElement.setVelocity(-velocity.getX(), velocity.getY());
    		}
    		if ((position.getY() > MODEL_BOUNDS.getMaxY() && velocity.getY() > 0) ||
        		(position.getY() < MODEL_BOUNDS.getMinY() && velocity.getY() < 0))	{
        		// Reverse direction in the Y direction.
        		modelElement.setVelocity(velocity.getX(), -velocity.getY());
        	}
    		
    		// Update the position based on the velocity.
    		modelElement.updatePosition();
    	}

    	// Update the position of each of the composite model elements.
    	for (CompositeModelElement componsiteModelElement : compositeModelElements){
    		
    		Point2D position = componsiteModelElement.getPositionRef();
    		Vector2D velocity = componsiteModelElement.getVelocityRef();
    		
    		if ((position.getX() > MODEL_BOUNDS.getMaxX() && velocity.getX() > 0) ||
    			(position.getX() < MODEL_BOUNDS.getMinX() && velocity.getX() < 0))	{
    			// Reverse direction in the X direction.
    			componsiteModelElement.setVelocity(-velocity.getX(), velocity.getY());
    		}
    		if ((position.getY() > MODEL_BOUNDS.getMaxY() && velocity.getY() > 0) ||
        		(position.getY() < MODEL_BOUNDS.getMinY() && velocity.getY() < 0))	{
        		// Reverse direction in the Y direction.
    			componsiteModelElement.setVelocity(velocity.getX(), -velocity.getY());
        	}
    		
    		// Update the position based on the velocity.
    		componsiteModelElement.updatePosition();
    	}
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------
	
    protected void notifyModelElementAdded(SimpleModelElement modelElement){
        // Notify all listeners of the addition of this model element.
        for (Listener listener : listeners)
        {
            listener.modelElementAdded(modelElement); 
        }        
    }

    public void addListener(Listener listener) {
        if (listeners.contains( listener ))
        {
            // Don't bother re-adding.
        	System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
        	assert false;
            return;
        }
        
        listeners.add( listener );
    }
    
    public void removeListener(Listener listener){
    	listeners.remove(listener);
    }
	
    public interface Listener {
        void modelElementAdded(SimpleModelElement modelElement);
    }
    
    /**
     * Class that defines model elements that are composed of a collection of
     * simple model elements.
     */
    private static class CompositeModelElement implements IModelElement {
    	
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
    }
}
