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
    protected ArrayList<Listener> listeners = new ArrayList<Listener>();
    
    // Lists of model elements for which multiple instances can exist.
    private final ArrayList<LacI> lacIList = new ArrayList<LacI>();
    private final ArrayList<LacZ> lacZList = new ArrayList<LacZ>();
    private final ArrayList<Glucose> glucoseList = new ArrayList<Glucose>();
    private final ArrayList<Galactose> galactoseList = new ArrayList<Galactose>();
    private final ArrayList<RnaPolymerase> rnaPolymeraseList = new ArrayList<RnaPolymerase>();
    
    // Lists of model elements for which only one instance can exist.
    private final Cap cap = new Cap();
    private final CapBindingRegion capBindingRegion = new CapBindingRegion();
    private final LacOperator lacOperator = new LacOperator();
    private final LacIGene lacIGene = new LacIGene();
    private final LacZGene lacZGene = new LacZGene();
    private final LacYGene lacYGene = new LacYGene();
    private final LacIPromoter lacIPromoter = new LacIPromoter();

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
	public LacOperonModel( GeneNetworkClock clock ) {
        super();
        
        this.clock = clock;        
        
        clock.addClockListener(new ClockAdapter(){

			@Override
			public void clockTicked(ClockEvent clockEvent) {
				handleClockTicked(clockEvent.getSimulationTimeChange());
			}
        });
        
        addInitialModelElements();
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

	public ArrayList<LacI> getLacIList() {
		return lacIList;
	}

	public ArrayList<LacZ> getLacZList() {
		return lacZList;
	}

	public ArrayList<Glucose> getGlucoseList() {
		return glucoseList;
	}

	public ArrayList<Galactose> getGalactoseList() {
		return galactoseList;
	}

	public ArrayList<RnaPolymerase> getRnaPolymeraseList() {
		return rnaPolymeraseList;
	}

	public Cap getCap() {
		return cap;
	}

	public CapBindingRegion getCapBindingRegion() {
		return capBindingRegion;
	}

	public LacOperator getLacOperator() {
		return lacOperator;
	}

	public LacIGene getLacIGene() {
		return lacIGene;
	}

	public LacZGene getLacZGene() {
		return lacZGene;
	}

	public LacYGene getLacYGene() {
		return lacYGene;
	}

	public LacIPromoter getLacIPromoter() {
		return lacIPromoter;
	}

	private void addInitialModelElements() {
		
        // Initialize the elements that are floating around the cell.
		
        randomlyInitModelElement(cap);

        for (int i = 0; i < 4; i++){
        	RnaPolymerase rnaPolymerase = new RnaPolymerase();
        	randomlyInitModelElement(rnaPolymerase);
        	rnaPolymeraseList.add(rnaPolymerase);
        }
        
        // Create and position the elements that sit on the DNA strand.
        
        double xPosition = DNA_STRAND_LOCATION.getMinX(); // Start at the far left side of the strand.
        
        xPosition += 3; // Add a little space on far left side.
        
        xPosition += lacIPromoter.getShape().getBounds2D().getWidth() / 2;
        lacIPromoter.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        xPosition += lacIPromoter.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += 2; // The spec shows a little bit of space here.
        
        xPosition += lacIGene.getShape().getBounds2D().getWidth() / 2;
        lacIGene.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        xPosition += lacIGene.getShape().getBounds2D().getWidth() / 2;
        
        xPosition = DNA_STRAND_LOCATION.getMinX() + 0.45 * DNA_STRAND_LOCATION.getWidth();
        
        xPosition += capBindingRegion.getShape().getBounds2D().getWidth() / 2;
        capBindingRegion.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        xPosition += capBindingRegion.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += lacIPromoter.getShape().getBounds2D().getWidth() / 2;
        lacIPromoter.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        xPosition += lacIPromoter.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += lacOperator.getShape().getBounds2D().getWidth() / 2;
        lacOperator.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        xPosition += lacOperator.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += 2; // The spec shows some space here.

        xPosition += lacZGene.getShape().getBounds2D().getWidth() / 2;
        lacZGene.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        xPosition += lacZGene.getShape().getBounds2D().getWidth() / 2;
        
        xPosition += 1; // The spec shows some space here.

        xPosition += lacYGene.getShape().getBounds2D().getWidth() / 2;
        lacYGene.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        xPosition += lacYGene.getShape().getBounds2D().getWidth() / 2;
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

    /*
     * TODO: Uncomment and get working or get rid of it.
    private void addLactoseMolecule(){
        Lactose lactose = new Lactose();
        lactose.setPosition((RAND.nextDouble() - 0.5) * (MODEL_AREA_WIDTH / 2), 
    			(RAND.nextDouble() - 0.5) * (MODEL_AREA_HEIGHT / 2));
    	double maxVel = 2;
    	lactose.setVelocity((RAND.nextDouble() - 0.5) * maxVel, (RAND.nextDouble() - 0.5) * maxVel);
        addModelElement(lactose);
    }
     */
    
    private void randomlyInitModelElement(SimpleModelElement modelElement){
    	modelElement.setPosition((RAND.nextDouble() - 0.5) * (MODEL_AREA_WIDTH / 2), 
    			(RAND.nextDouble() - 0.5) * (MODEL_AREA_HEIGHT / 2));
    	double maxVel = 2;
    	modelElement.setVelocity((RAND.nextDouble() - 0.5) * maxVel, (RAND.nextDouble() - 0.5) * maxVel);
    }
    
    /**
     * Get a list of all simple model elements in the model.
     * 
     * @return
     */
    public ArrayList<SimpleModelElement> getAllSimpleModelElements(){
    	ArrayList<SimpleModelElement> allSimples = new ArrayList<SimpleModelElement>();
    	allSimples.addAll(rnaPolymeraseList);
    	allSimples.addAll(lacIList);
    	allSimples.addAll(lacZList);
    	allSimples.addAll(glucoseList);
    	allSimples.addAll(galactoseList);
    	allSimples.add(cap);
    	allSimples.add(capBindingRegion);
    	allSimples.add(lacOperator);
    	allSimples.add(lacIGene);
    	allSimples.add(lacYGene);
    	allSimples.add(lacZGene);
    	allSimples.add(lacIPromoter);
    	return allSimples;
    }
    
    private void handleClockTicked(double dt){
    	
    	for (LacZ lacZ : lacZList){
    		lacZ.updatePositionAndMotion(dt);
    	}
    	
    	for (LacI lacI : lacIList){
    		lacI.updatePositionAndMotion(dt);
    	}
    	
    	for (Glucose glucose : glucoseList){
    		glucose.updatePositionAndMotion(dt);
    	}
    	
    	for (Galactose galactose : galactoseList){
    		galactose.updatePositionAndMotion(dt);
    	}
    	
    	for (RnaPolymerase rnaPolymerase : rnaPolymeraseList){
    		rnaPolymerase.updatePositionAndMotion(dt);
    	}
    	
    	cap.updatePositionAndMotion(dt);
    	capBindingRegion.updatePositionAndMotion(dt);
    	lacOperator.updatePositionAndMotion(dt);
    	lacIGene.updatePositionAndMotion(dt);
    	lacYGene.updatePositionAndMotion(dt);
    	lacZGene.updatePositionAndMotion(dt);
    	lacIPromoter.updatePositionAndMotion(dt);
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
