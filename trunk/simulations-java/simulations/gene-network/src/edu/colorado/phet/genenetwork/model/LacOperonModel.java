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

	// Constants that defines where in the model space the DNA strand will be.
	private static final double DNA_STRAND_WIDTH = MODEL_AREA_WIDTH * 1.3;
	private static final double DNA_STRAND_HEIGHT = 4;  // In nanometers.
	private static final Rectangle2D DNA_STRAND_LOCATION = new Rectangle2D.Double(-DNA_STRAND_WIDTH / 2, 
			-MODEL_AREA_HEIGHT * 0.4, DNA_STRAND_WIDTH, DNA_STRAND_HEIGHT);
	
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
        
        // Create and add the elements that are floating around the cell.
        
        for (int i = 0; i<4; i++){
        	modelElement = new LacZ();
        	randomlyInitAndAddSimpleModelElement(modelElement);
        }
        for (int i = 0; i<4; i++){
        	modelElement = new LacI();
        	randomlyInitAndAddSimpleModelElement(modelElement);
        }
        
        modelElement = new Cap();
        randomlyInitAndAddSimpleModelElement(modelElement);
        
        modelElement = new RnaPolymerase();
        randomlyInitAndAddSimpleModelElement(modelElement);
        
        modelElement = new MessengerRna();
        randomlyInitAndAddSimpleModelElement(modelElement);
        
        // Create and position the elements that sit on the DNA strand.
        
        double xPosition = DNA_STRAND_LOCATION.getMinX(); // Start at the far left side of the strand.
        
        xPosition += 5; // Add a little space on far left side.
        
        modelElement = new LacIPromoter();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addSimpleModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += 2; // The spec shows a little bit of space here.
        
        modelElement = new LacIGene();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addSimpleModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        xPosition = DNA_STRAND_LOCATION.getCenterX();
        modelElement = new CapBindingRegion();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addSimpleModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        modelElement = new LacPromoter();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addSimpleModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        modelElement = new LacOperator();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addSimpleModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += 3; // The spec shows some space here.

        modelElement = new LacZGene();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addSimpleModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        modelElement = new LacYGene();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addSimpleModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        
        // Add composite elements.
        
        // Lactose.
        for (int i = 0; i < 5; i++){
        	addLactoseMolecule();
        }
	}

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GeneNetworkClock getClock() {
        return clock;
    }

    /**
     * Get the location and size of the DNA strand.  The DNA strand is unique
     * amongst the model elements in that there is only one, it does not move,
     * and the shape of its representation is not specified in the model.
     * This is because nothing really binds directly to it, but to the sub-
     * portions of the strand that ARE specified as model elements.
     * 
     * @return
     */
    public Rectangle2D getDnaPosition(){
    	return new Rectangle2D.Double(DNA_STRAND_LOCATION.getX(), DNA_STRAND_LOCATION.getY(),
    			DNA_STRAND_LOCATION.getWidth(), DNA_STRAND_LOCATION.getHeight());
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
    
    private void addLactoseMolecule(){
        ArrayList<SimpleModelElement> compositeList = new ArrayList<SimpleModelElement>();
        compositeList.add(new Galactose());
        compositeList.add(new Glucose());
        CompositeModelElement compositeModelElement = 
        	new CompositeModelElement(compositeList, new Point2D.Double(0, 0));
        compositeModelElement.setPosition((RAND.nextDouble() - 0.5) * (MODEL_AREA_WIDTH / 2), 
    			(RAND.nextDouble() - 0.5) * (MODEL_AREA_HEIGHT / 2));
    	double maxVel = 2;
    	compositeModelElement.setVelocity((RAND.nextDouble() - 0.5) * maxVel, (RAND.nextDouble() - 0.5) * maxVel);
        addCompositeModelElement(compositeModelElement);
    }
    
    private void randomlyInitAndAddSimpleModelElement(SimpleModelElement modelElement){
    	modelElement.setPosition((RAND.nextDouble() - 0.5) * (MODEL_AREA_WIDTH / 2), 
    			(RAND.nextDouble() - 0.5) * (MODEL_AREA_HEIGHT / 2));
    	double maxVel = 2;
    	modelElement.setVelocity((RAND.nextDouble() - 0.5) * maxVel, (RAND.nextDouble() - 0.5) * maxVel);
    	addSimpleModelElement(modelElement);
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
