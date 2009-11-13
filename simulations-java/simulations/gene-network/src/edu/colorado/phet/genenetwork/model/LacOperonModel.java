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
public class LacOperonModel implements IObtainGeneModelElements {
    
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	private static final double MODEL_AREA_WIDTH = 100;
	private static final double MODEL_AREA_HEIGHT = 100;
	private static final Rectangle2D MODEL_BOUNDS = new Rectangle2D.Double(-MODEL_AREA_WIDTH / 2,
			-MODEL_AREA_HEIGHT / 2, MODEL_AREA_WIDTH, MODEL_AREA_HEIGHT);
	private static final Random RAND = new Random();

	// Constants that define where in the model space the DNA strand will be.
	private static final double DNA_STRAND_WIDTH = MODEL_AREA_WIDTH * 1.3;
	private static final double DNA_STRAND_HEIGHT = 3;  // In nanometers.
	private static final Rectangle2D DNA_STRAND_LOCATION = new Rectangle2D.Double(-DNA_STRAND_WIDTH / 2, 
			-MODEL_AREA_HEIGHT * 0.4, DNA_STRAND_WIDTH, DNA_STRAND_HEIGHT);
	
	// Constant that defines where the mobile model elements can go.
	private static final Rectangle2D MOTION_BOUNDS = new Rectangle2D.Double(MODEL_BOUNDS.getMinX(), 
			DNA_STRAND_LOCATION.getCenterY(), MODEL_BOUNDS.getWidth(),
			MODEL_BOUNDS.getHeight() - DNA_STRAND_LOCATION.getMinY() + MODEL_BOUNDS.getMinY());
	
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
    private final ArrayList<MessengerRna> messengerRnaList = new ArrayList<MessengerRna>();
    
    // Lists of model elements for which only one instance can exist.
    private final Cap cap = new Cap(this);
    private final CapBindingRegion capBindingRegion = new CapBindingRegion(this);
    private final LacOperator lacOperator = new LacOperator(this);
    private final LacIGene lacIGene = new LacIGene(this);
    private final LacZGene lacZGene = new LacZGene(this);
    private final LacYGene lacYGene = new LacYGene(this);
    private final LacIPromoter lacIPromoter = new LacIPromoter(this);
    private final LacPromoter lacPromoter = new LacPromoter(this);

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

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getLacIList()
	 */
	public ArrayList<LacI> getLacIList() {
		return lacIList;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getLacZList()
	 */
	public ArrayList<LacZ> getLacZList() {
		return lacZList;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getGlucoseList()
	 */
	public ArrayList<Glucose> getGlucoseList() {
		return glucoseList;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getGalactoseList()
	 */
	public ArrayList<Galactose> getGalactoseList() {
		return galactoseList;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getRnaPolymeraseList()
	 */
	public ArrayList<RnaPolymerase> getRnaPolymeraseList() {
		return rnaPolymeraseList;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getCap()
	 */
	public Cap getCap() {
		return cap;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getCapBindingRegion()
	 */
	public CapBindingRegion getCapBindingRegion() {
		return capBindingRegion;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getLacOperator()
	 */
	public LacOperator getLacOperator() {
		return lacOperator;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getLacIGene()
	 */
	public LacIGene getLacIGene() {
		return lacIGene;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getLacZGene()
	 */
	public LacZGene getLacZGene() {
		return lacZGene;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getLacYGene()
	 */
	public LacYGene getLacYGene() {
		return lacYGene;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getLacIPromoter()
	 */
	public LacIPromoter getLacIPromoter() {
		return lacIPromoter;
	}

	private void addInitialModelElements() {
		
        // Initialize the elements that are floating around the cell.
		
        randomlyInitModelElement(cap);

        for (int i = 0; i < 4; i++){
        	RnaPolymerase rnaPolymerase = new RnaPolymerase(this);
        	randomlyInitModelElement(rnaPolymerase);
        	rnaPolymeraseList.add(rnaPolymerase);
        }
        
        // Create some other model elements needed for testing.  TODO: This is
        // done for debugging and should be removed at some point.
        LacI lacIForTesting = new LacI(this);
        randomlyInitModelElement(lacIForTesting);
        lacIList.add(lacIForTesting);
        
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
        
        xPosition += lacPromoter.getShape().getBounds2D().getWidth() / 2;
        lacPromoter.setPosition(xPosition, DNA_STRAND_LOCATION.getCenterY());
        xPosition += lacPromoter.getShape().getBounds2D().getWidth() / 2;
        
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
        
        // Force the CAP to attach to the CAP binding region on the DNA.
        if (cap.considerProposalFrom(capBindingRegion) == true){
        	cap.attach(capBindingRegion);
        }
        else{
        	System.err.println(getClass().getName() + " - Error: Unable to attach CAP to CAP binding region.");
        }
	}

    public GeneNetworkClock getClock() {
        return clock;
    }
    
    public static Rectangle2D getMotionBounds(){
    	return MOTION_BOUNDS;
    }
    
    public boolean isLacIAttachedToDna(){
    	return lacOperator.isLacIAttached();
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
    
    /* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getAllSimpleModelElements()
	 */
    public ArrayList<SimpleModelElement> getAllSimpleModelElements(){
    	ArrayList<SimpleModelElement> allSimples = new ArrayList<SimpleModelElement>();
    	allSimples.addAll(rnaPolymeraseList);
    	allSimples.addAll(lacIList);
    	allSimples.addAll(lacZList);
    	allSimples.addAll(glucoseList);
    	allSimples.addAll(galactoseList);
    	allSimples.addAll(messengerRnaList);
    	allSimples.add(cap);
    	allSimples.add(capBindingRegion);
    	allSimples.add(lacOperator);
    	allSimples.add(lacIGene);
    	allSimples.add(lacYGene);
    	allSimples.add(lacZGene);
    	allSimples.add(lacIPromoter);
    	allSimples.add(lacPromoter);
    	return allSimples;
    }
    
    public void addMessengerRna(MessengerRna messengerRna){
    	messengerRnaList.add(messengerRna);
    	notifyModelElementAdded(messengerRna);
    }
    
    private void handleClockTicked(double dt){
    	
    	for (LacZ lacZ : lacZList){
    		lacZ.stepInTime(dt);
    	}
    	
    	for (LacI lacI : lacIList){
    		lacI.stepInTime(dt);
    	}
    	
    	for (Glucose glucose : glucoseList){
    		glucose.stepInTime(dt);
    	}
    	
    	for (Galactose galactose : galactoseList){
    		galactose.stepInTime(dt);
    	}
    	
    	for (RnaPolymerase rnaPolymerase : rnaPolymeraseList){
    		rnaPolymerase.stepInTime(dt);
    	}
    	
    	cap.stepInTime(dt);
    	capBindingRegion.stepInTime(dt);
    	lacOperator.stepInTime(dt);
    	lacIGene.stepInTime(dt);
    	lacYGene.stepInTime(dt);
    	lacZGene.stepInTime(dt);
    	lacIPromoter.stepInTime(dt);
    	lacPromoter.stepInTime(dt);

        for (MessengerRna m : messengerRnaList) m.grow(0.3);
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
