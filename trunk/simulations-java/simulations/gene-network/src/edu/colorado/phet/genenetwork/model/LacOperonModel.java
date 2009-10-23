/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
        SimpleModelElement modelElement = new LacZ(new Point2D.Double(0, 0));
        modelElement.setVelocity(1, 1.5);
        addModelElement(modelElement);
        modelElement = new LacZ(new Point2D.Double(3, 3));
        modelElement.setVelocity(-1, 1.5);
        addModelElement(modelElement);
        modelElement = new Glucose(new Point2D.Double(20, 20));
        modelElement.setVelocity(1, 0.5);
        addModelElement(modelElement);
        modelElement = new Glucose(new Point2D.Double(20, 20));
        modelElement.setVelocity(1, 0.5);
        addModelElement(modelElement);
        modelElement = new Galactose(new Point2D.Double(-20, -10));
        modelElement.setVelocity(-1.5, 1);
        addModelElement(modelElement);
        modelElement = new Galactose(new Point2D.Double(15, -10));
        modelElement.setVelocity(-1.5, 1);
        addModelElement(modelElement);
        modelElement = new LacI(new Point2D.Double(20, 10));
        modelElement.setVelocity(1.5, 1);
        addModelElement(modelElement);
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
