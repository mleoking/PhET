/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Primary model for the Lac Operon flavor of this sim.
 */
public class LacOperonModel implements IGeneNetworkModelControl {
    
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	private static final double MODEL_AREA_WIDTH = 120;
	private static final double MODEL_AREA_HEIGHT = 100;
	private static final Rectangle2D MODEL_BOUNDS = new Rectangle2D.Double(-MODEL_AREA_WIDTH / 2,
			-MODEL_AREA_HEIGHT / 2, MODEL_AREA_WIDTH, MODEL_AREA_HEIGHT);
	private static final Random RAND = new Random();

	// Constants that define where in the model space the DNA strand will be.
	private static final double DNA_STRAND_WIDTH = MODEL_AREA_WIDTH * 1.3;
	private static final double DNA_STRAND_HEIGHT = 1.5;  // In nanometers.
	private static final Dimension2D DNA_STRAND_SIZE = new PDimension(DNA_STRAND_WIDTH, DNA_STRAND_HEIGHT);
	private static final Point2D DNA_STRAND_POSITION = new Point2D.Double(0, -20);
	
	// Constant that defines where the mobile model elements can go.
	private static final Rectangle2D MOTION_BOUNDS = new Rectangle2D.Double(MODEL_BOUNDS.getMinX(), 
			DNA_STRAND_POSITION.getY(), MODEL_BOUNDS.getWidth(),
			MODEL_BOUNDS.getHeight() - DNA_STRAND_POSITION.getY() + MODEL_BOUNDS.getMinY());
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final GeneNetworkClock clock;
    protected ArrayList<GeneNetworkModelListener> listeners = new ArrayList<GeneNetworkModelListener>();
    
    // The DNA strand.
    private final DnaStrand dnaStrand = new DnaStrand( DNA_STRAND_SIZE, DNA_STRAND_POSITION );
    
    // Lists of simple model elements for which multiple instances can exist.
    private final ArrayList<LacI> lacIList = new ArrayList<LacI>();
    private final ArrayList<LacZ> lacZList = new ArrayList<LacZ>();
    private final ArrayList<Glucose> glucoseList = new ArrayList<Glucose>();
    private final ArrayList<Galactose> galactoseList = new ArrayList<Galactose>();
    private final ArrayList<RnaPolymerase> rnaPolymeraseList = new ArrayList<RnaPolymerase>();
    private final ArrayList<MessengerRna> messengerRnaList = new ArrayList<MessengerRna>();
    private final ArrayList<TransformationArrow> transformationArrowList = new ArrayList<TransformationArrow>();
    
    // Lists of simple model elements for which only one instance can exist.
    private Cap cap = null;
    private CapBindingRegion capBindingRegion = null;
    private LacOperator lacOperator = null;
    private LacIGene lacIGene = null;
    private LacZGene lacZGene = null;
    private LacYGene lacYGene = null;
    private LacIPromoter lacIPromoter = null;
    private LacPromoter lacPromoter = null;

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
        
        // TODO: As of Nov 23, 2009, it was decided that the sim should start
        // out with just the DNA showing, and the users should build up the
        // gene network.  However, it is probably worthwhile to keep the code
        // that adds the initial model elements around for a while for testing
        // purposes.  So it is commented out for now - please delete it
        // permanently when appropriate.
        
//        addInitialModelElements();
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

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getLacPromoter()
	 */
	public LacPromoter getLacPromoter() {
		return lacPromoter;
	}

	private void addInitialModelElements() {
		
        // Initialize the elements that are floating around the cell.
		
        randomlyInitModelElement(cap);

        for (int i = 0; i < 2; i++){
        	RnaPolymerase rnaPolymerase = new RnaPolymerase(this);
        	randomlyInitModelElement(rnaPolymerase);
        	rnaPolymeraseList.add(rnaPolymerase);
        }
        
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
    	if (lacOperator == null){
    		return false;
    	}
    	else{
    		return lacOperator.isLacIAttached();
    	}
    }
    
    public boolean isLacOperatorPresent(){
    	return lacOperator != null;
    }

    public boolean isLacZGenePresent(){
    	return lacZGene != null;
    }

	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IObtainModelElements#getDnaStrand()
	 */
    public DnaStrand getDnaStrand(){
    	return dnaStrand;
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
    	allSimples.addAll(transformationArrowList);
    	if (cap != null){
    		allSimples.add(cap);
    	}
    	if (capBindingRegion != null){
    		allSimples.add(capBindingRegion);
    	}
    	if (lacOperator != null){
    		allSimples.add(lacOperator);
    	}
    	if (lacIGene != null){
    		allSimples.add(lacIGene);
    	}
    	if (lacYGene != null){
    		allSimples.add(lacYGene);
    	}
    	if (lacZGene != null){
    		allSimples.add(lacZGene);
    	}
    	if (lacIPromoter != null){
    		allSimples.add(lacIPromoter);
    	}
    	if (lacPromoter != null){
    		allSimples.add(lacPromoter);
    	}

    	return allSimples;
    }
    
    public void addMessengerRna(MessengerRna messengerRna){
    	messengerRnaList.add(messengerRna);
    	notifyModelElementAdded(messengerRna);
    }
    
    public void addTransformationArrow(TransformationArrow transformationArrow){
    	transformationArrowList.add(transformationArrow);
    	notifyModelElementAdded(transformationArrow);
    }
    
    public void addLacZ(LacZ lacZ){
    	lacZList.add(lacZ);
    	notifyModelElementAdded(lacZ);
    }
    
    public void addLacI(LacI lacI){
    	lacIList.add(lacI);
    	notifyModelElementAdded(lacI);
    }
    
	public LacZGene createAndAddLacZGene(Point2D initialPosition) {
		assert lacZGene == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
		lacZGene = new LacZGene(this, initialPosition);
		notifyModelElementAdded(lacZGene);
		return lacZGene;
	}
    
	public LacIGene createAndAddLacIGene(Point2D initialPosition) {
		assert lacIGene == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
		lacIGene = new LacIGene(this, initialPosition);
		notifyModelElementAdded(lacIGene);
		return lacIGene;
	}
    
	public LacOperator createAndAddLacOperator(Point2D initialPosition) {
		assert lacOperator == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
		lacOperator = new LacOperator(this, initialPosition);
		notifyModelElementAdded(lacOperator);
		return lacOperator;
	}
    
	public LacPromoter createAndAddLacPromoter(Point2D initialPosition) {
		assert lacPromoter == null; // Only one exists in this model, so multiple calls to this shouldn't occur.
		lacPromoter = new LacPromoter(this, initialPosition);
		notifyModelElementAdded(lacPromoter);
		return lacPromoter;
	}
    
	public RnaPolymerase createAndAddRnaPolymerase(Point2D initialPosition) {
		RnaPolymerase rnaPolymerase = new RnaPolymerase(this, initialPosition);
		rnaPolymeraseList.add(rnaPolymerase);
		notifyModelElementAdded(rnaPolymerase);
		return rnaPolymerase;
	}
    
    private void handleClockTicked(double dt){

    	// Step the elements for which there can be multiple instances.
    	stepElementsInTime(lacZList, dt);
    	stepElementsInTime(lacIList, dt);
    	stepElementsInTime(glucoseList, dt);
    	stepElementsInTime(galactoseList, dt);
    	stepElementsInTime(rnaPolymeraseList, dt);
    	stepElementsInTime(messengerRnaList, dt);
    	stepElementsInTime(transformationArrowList, dt);
    	
    	// Step the elements for which there can be only one.
    	if (cap != null){
    		cap.stepInTime(dt);
    	}
    	if (capBindingRegion != null){
    		capBindingRegion.stepInTime(dt);
    	}
    	if (lacOperator != null){
    		lacOperator.stepInTime(dt);
    	}
    	if (lacIGene != null){
    		lacIGene.stepInTime(dt);
    	}
    	if (lacYGene != null){
    		lacYGene.stepInTime(dt);
    	}
    	if (lacZGene != null){
    		lacZGene.stepInTime(dt);
    	}
    	if (lacIPromoter != null){
    		lacIPromoter.stepInTime(dt);
    	}
    	if (lacPromoter != null){
    		lacPromoter.stepInTime(dt);
    	}
    }
    
    private void stepElementsInTime(ArrayList<? extends IModelElement>elements, double dt){
    	ArrayList<IModelElement> toBeRemoved = new ArrayList<IModelElement>();
    	for (IModelElement element : elements){
    		element.stepInTime(dt);
    		if (element.getExistenceStrength() <= 0){
    			// If a model element gets to the point where its existence
    			// strength is zero, it has essentially died, or dissolved, or
    			// just "ceased to be", so should be removed from the model.
    			toBeRemoved.add(element);
    			element.removeFromModel();
    		}
    	}
    	elements.removeAll(toBeRemoved);
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------
	
    protected void notifyModelElementAdded(SimpleModelElement modelElement){
        // Notify all listeners of the addition of this model element.
        for (GeneNetworkModelListener listener : listeners)
        {
            listener.modelElementAdded(modelElement); 
        }        
    }

    public void addListener(GeneNetworkModelListener listener) {
        if (listeners.contains( listener ))
        {
            // Don't bother re-adding.
        	System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
        	assert false;
            return;
        }
        
        listeners.add( listener );
    }
    
    public void removeListener(GeneNetworkModelListener listener){
    	listeners.remove(listener);
    }
}
