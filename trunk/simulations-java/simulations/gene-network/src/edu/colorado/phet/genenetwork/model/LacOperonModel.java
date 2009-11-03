/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

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
	private static final double DNA_STRAND_HEIGHT = 3;  // In nanometers.
	private static final Rectangle2D DNA_STRAND_LOCATION = new Rectangle2D.Double(-DNA_STRAND_WIDTH / 2, 
			-MODEL_AREA_HEIGHT * 0.4, DNA_STRAND_WIDTH, DNA_STRAND_HEIGHT);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final GeneNetworkClock clock;
    private ArrayList<IModelElement> modelElements = new ArrayList<IModelElement>();
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
        
        xPosition += 3; // Add a little space on far left side.
        
        modelElement = new LacIPromoter();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += 2; // The spec shows a little bit of space here.
        
        modelElement = new LacIGene();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        xPosition = DNA_STRAND_LOCATION.getMinX() + 0.45 * DNA_STRAND_LOCATION.getWidth();
        
        modelElement = new CapBindingRegion();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        modelElement = new LacPromoter();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        modelElement = new LacOperator();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += 2; // The spec shows some space here.

        modelElement = new LacZGene();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addModelElement(modelElement);
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += 1; // The spec shows some space here.

        modelElement = new LacYGene();
        xPosition += modelElement.getShape().getBounds2D().getWidth() / 2;
        modelElement.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        addModelElement(modelElement);
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
    
    public static Rectangle2D getModelBounds(){
    	return MODEL_BOUNDS;
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
    
    private void addModelElement(IModelElement modelElement){
    	modelElements.add(modelElement);
    }
    
    private void addLactoseMolecule(){
        Lactose lactose = new Lactose();
        lactose.setPosition((RAND.nextDouble() - 0.5) * (MODEL_AREA_WIDTH / 2), 
    			(RAND.nextDouble() - 0.5) * (MODEL_AREA_HEIGHT / 2));
    	double maxVel = 2;
    	lactose.setVelocity((RAND.nextDouble() - 0.5) * maxVel, (RAND.nextDouble() - 0.5) * maxVel);
        addModelElement(lactose);
    }
    
    private void randomlyInitAndAddSimpleModelElement(SimpleModelElement modelElement){
    	modelElement.setPosition((RAND.nextDouble() - 0.5) * (MODEL_AREA_WIDTH / 2), 
    			(RAND.nextDouble() - 0.5) * (MODEL_AREA_HEIGHT / 2));
    	double maxVel = 2;
    	modelElement.setVelocity((RAND.nextDouble() - 0.5) * maxVel, (RAND.nextDouble() - 0.5) * maxVel);
    	addModelElement(modelElement);
    }
    
    /**
     * Get a list of all simple model elements in the model, including any
     * that are currently bound up in composite model elements.
     * @return
     */
    public ArrayList<SimpleModelElement> getAllSimpleModelElements(){
    	ArrayList<SimpleModelElement> allSimples = new ArrayList<SimpleModelElement>();
    	for (IModelElement modelElement : modelElements){
    		if (modelElement instanceof SimpleModelElement){
    			allSimples.add((SimpleModelElement)modelElement);
    		}
    		else{
    			allSimples.addAll(((CompositeModelElement)modelElement).getSimpleElementConstituents());
    		}
    	}
    	return allSimples;
    }
    
    private void handleClockTicked(){
    	// Update the position of each of the simple model elements.
    	for (IModelElement modelElement : modelElements){
    		// Update the current position and velocity (including direction).
    		modelElement.updatePositionAndMotion();
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
