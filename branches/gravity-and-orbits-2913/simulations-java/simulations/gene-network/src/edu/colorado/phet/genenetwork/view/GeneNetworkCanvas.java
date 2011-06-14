// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkConstants;
import edu.colorado.phet.genenetwork.model.Galactose;
import edu.colorado.phet.genenetwork.model.GeneNetworkModelAdapter;
import edu.colorado.phet.genenetwork.model.Glucose;
import edu.colorado.phet.genenetwork.model.LacOperonModel;
import edu.colorado.phet.genenetwork.model.MessengerRna;
import edu.colorado.phet.genenetwork.model.ModelElementListenerAdapter;
import edu.colorado.phet.genenetwork.model.RnaPolymerase;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.colorado.phet.genenetwork.model.TransformationArrow;
import edu.colorado.phet.genenetwork.module.LacOperonDefaults;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas template.
 */
public abstract class GeneNetworkCanvas extends PhetPCanvas {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	// Initial size of the reference coordinates that are used when setting up
	// the canvas transform strategy.  These were empirically determined to
	// roughly match the expected initial size of the canvas.
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private static final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Model-view transform.
    private final ModelViewTransform2D mvt;
    
	// Layers for creating the desired overlap behavior.
    private final PNode cellularFluidLayer;
    private final PNode toolBoxLayer;
	private final PNode dnaStrandLayer;
    private final PNode backmostRovingModelElementLayer; // Backmost layer for roving elements.
    private final PNode middleRovingModelElementLayer;
    private final PNode frontmostRovingModelElementLayer; // Frontmost layer for roving elements.
    private final PNode lactoseInjectorLayer;
    private final PNode cellMembraneLayer;
    private final PNode legendLayer;
    
    // Various non-moving nodes that exist on the canvas.
	private MacroMoleculeLegend legend;
	private LactoseMeter lactoseMeter;
	private LactoseInjectorNode lactoseInjector;
	private DnaStrandNode dnaStrand;
	private DnaSegmentToolBoxNode toolBox;
	private PNode cellMembraneNode;
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
    
	public GeneNetworkCanvas( LacOperonModel model ) {
        super( LacOperonDefaults.VIEW_SIZE );
        
        setBackground( GeneNetworkConstants.CANVAS_BACKGROUND );
        
    	// Set up the canvas-screen transform.
    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
    	
    	// Set up the model-canvas transform.
        mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.55 )),
        		7,  // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        		true);
        
        // Register for notifications from the model.
        model.addListener(new GeneNetworkModelAdapter() {
			public void modelElementAdded(SimpleModelElement modelElement) {
				addModelElement(modelElement);
			}
		});
        
        // Create and add the layers.
        cellularFluidLayer = new PNode();
        addWorldChild(cellularFluidLayer);
        toolBoxLayer = new PNode();
        addScreenChild(toolBoxLayer);
        dnaStrandLayer = new PNode();
        addWorldChild(dnaStrandLayer);
        backmostRovingModelElementLayer = new PNode();
        addWorldChild(backmostRovingModelElementLayer);
        middleRovingModelElementLayer = new PNode();
        addWorldChild(middleRovingModelElementLayer);
        frontmostRovingModelElementLayer = new PNode();
        addWorldChild(frontmostRovingModelElementLayer);
        lactoseInjectorLayer = new PNode();
        addWorldChild(lactoseInjectorLayer);
        cellMembraneLayer = new PNode();
        addWorldChild(cellMembraneLayer);
        legendLayer = new PNode();
        addScreenChild(legendLayer);
        
        // Add any simple model elements (which are, in general, the things
        // that move around) that are already present in the model.
        for (SimpleModelElement modelElement : model.getAllSimpleModelElements()){
        	addModelElement(modelElement);
        }
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
	protected ModelViewTransform2D getMvt() {
		return mvt;
	}
	
	protected void setToolBox(DnaSegmentToolBoxNode toolBox) {
		if (this.toolBox != null){
			// Remove the old one.
			toolBoxLayer.removeChild(this.toolBox);
		}
		// Add this to the canvas and keep a reference.
		toolBoxLayer.addChild(toolBox);
		this.toolBox = toolBox;
	}

	protected void setCellMembraneNode(PNode cellMembraneNode) {
		if (this.cellMembraneNode != null){
			// Remove the old one.
			cellMembraneLayer.removeChild(this.cellMembraneNode);
		}
		// Add this to the canvas and keep a reference.
		cellMembraneLayer.addChild(cellMembraneNode);
		this.cellMembraneNode = cellMembraneNode;
		
		// Create a background above the cell membrane that looks distinct
		// from the cell interior.
		cellularFluidLayer.removeAllChildren();
		double fluidBackgroundWidth = INITIAL_INTERMEDIATE_COORD_WIDTH * 3;
		double fluidBackgroundHeight = INITIAL_INTERMEDIATE_COORD_HEIGHT;
		PNode extraCellularFluid = new PhetPPath(new Rectangle2D.Double(0, 0, fluidBackgroundWidth,
				fluidBackgroundHeight), new Color(204, 255, 249));
		extraCellularFluid.setOffset(INITIAL_INTERMEDIATE_COORD_WIDTH / 2 - fluidBackgroundWidth / 2, 
				cellMembraneNode.getFullBoundsReference().getMinY() - fluidBackgroundHeight);
		cellularFluidLayer.addChild(extraCellularFluid);
	}

	protected void setLegend(MacroMoleculeLegend legend) {
		if (this.legend != null){
			// Remove the old one.
			legendLayer.removeChild(this.legend);
		}
		// Add this to the canvas and keep a reference.
		legendLayer.addChild(legend);
		this.legend = legend;
	}

	protected void setLactoseMeter(LactoseMeter lactoseMeter) {
		if (this.lactoseMeter != null){
			// Remove the old one.
			lactoseInjectorLayer.removeChild(this.lactoseMeter);
		}
		// Add this to the canvas and keep a reference.
		lactoseInjectorLayer.addChild(lactoseMeter);
		this.lactoseMeter = lactoseMeter;
	}

	protected void setLactoseInjector(LactoseInjectorNode lactoseInjector) {
		if (this.lactoseInjector != null){
			// Remove the old one.
			lactoseInjectorLayer.removeChild(this.lactoseInjector);
		}
		// Add this to the canvas and keep a reference.
		lactoseInjectorLayer.addChild(lactoseInjector);
		this.lactoseInjector = lactoseInjector;
	}

	protected void setDnaStrand(DnaStrandNode dnaStrand) {
		if (this.dnaStrand != null){
			// Remove the old one.
			dnaStrandLayer.removeChild(this.dnaStrand);
		}
		// Add this to the canvas and keep a reference.
		dnaStrandLayer.addChild(dnaStrand);
		this.dnaStrand = dnaStrand;
	}

    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( GeneNetworkConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "cavas worldSize=" + worldSize );
        }
        
        // Place the legend where it needs to go.
        legend.setOffset(getWidth() - legend.getFullBoundsReference().getWidth() - 10, 10);
    }
    
    private void addModelElement(final SimpleModelElement modelElement){
    	
    	// Create the node that will represent this model element.
    	final SimpleModelElementNode modelElementNode = new SimpleModelElementNode(modelElement, mvt, true);
    	
    	if (modelElement.isPartOfDnaStrand()){
    		dnaStrandLayer.addChild(modelElementNode);
    	}
    	else{
    		if (modelElement instanceof Glucose || modelElement instanceof Galactose){
    			// Put lactose on the backmost layer.
    			backmostRovingModelElementLayer.addChild(modelElementNode);
    		}
    		else if (modelElement instanceof RnaPolymerase){
    			// Put RNA Polymerase on the foremost layer.
    			frontmostRovingModelElementLayer.addChild(modelElementNode);
    		}
    		else{
    			// Put everything else on the middle layer.
    			middleRovingModelElementLayer.addChild(modelElementNode);
    			
    			if (modelElement instanceof MessengerRna || modelElement instanceof TransformationArrow){
    				// These elements should not be movable by the user.
    				modelElementNode.setPickable(false);
    			}
    		}
    	}
    	
    	// Register for notification of removal from the model so that the
    	// corresponding node can be removed.
    	modelElement.addListener(new ModelElementListenerAdapter(){
    		@Override
    		public void removedFromModel() {
    	    	if ( backmostRovingModelElementLayer.getChildrenReference().contains(modelElementNode) ){
    	    		backmostRovingModelElementLayer.removeChild(modelElementNode);
    	    	}
    	    	else if ( middleRovingModelElementLayer.getChildrenReference().contains(modelElementNode) ){
    	    		middleRovingModelElementLayer.removeChild(modelElementNode);
    	    	}
    	    	else if ( frontmostRovingModelElementLayer.getChildrenReference().contains(modelElementNode) ){
    	    		frontmostRovingModelElementLayer.removeChild(modelElementNode);
    	    	}
    	    	else if ( dnaStrandLayer.getChildrenReference().contains(modelElementNode) ){
    	    		dnaStrandLayer.removeChild(modelElementNode);
    	    	}
    		}
    	});
    }    
}
