/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * Model template.
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
        
        // Add the initial model elements.
        for (int i = 0; i<4; i++){
        	SimpleModelElement modelElement = new LacZ();
        	randomlyInitModelElement(modelElement);
        	addModelElement(modelElement);
        }
        for (int i = 0; i<4; i++){
        	SimpleModelElement modelElement = new LacI();
        	randomlyInitModelElement(modelElement);
        	addModelElement(modelElement);
        }
        for (int i = 0; i<8; i++){
        	SimpleModelElement modelElement = new Glucose();
        	randomlyInitModelElement(modelElement);
        	addModelElement(modelElement);
        }
        for (int i = 0; i<8; i++){
        	SimpleModelElement modelElement = new Galactose();
        	randomlyInitModelElement(modelElement);
        	addModelElement(modelElement);
        }
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
    
    private void addModelElement(SimpleModelElement modelElement){
    	simpleModelElements.add(modelElement);
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
    	return new ArrayList<SimpleModelElement>(simpleModelElements);
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
}
