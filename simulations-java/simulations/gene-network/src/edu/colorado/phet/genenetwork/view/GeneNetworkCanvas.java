/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.genenetwork.GeneNetworkConstants;
import edu.colorado.phet.genenetwork.model.IObtainGeneModelElements;
import edu.colorado.phet.genenetwork.model.LacOperonModel;
import edu.colorado.phet.genenetwork.model.MessengerRna;
import edu.colorado.phet.genenetwork.model.ModelElementListenerAdapter;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.colorado.phet.genenetwork.module.LacOperonDefaults;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas template.
 */
public class GeneNetworkCanvas extends PhetPCanvas {
	
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

    // Model
    private IObtainGeneModelElements model;
    
    // View 
    private PNode rootNode;
    
    // Model-view transform.
    private ModelViewTransform2D mvt;
    
    // Layers for creating the desired overlap behavior.
    private PNode dnaStrandLayer;
    private PNode rovingModelElementLayer;
    
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
    
    public GeneNetworkCanvas( LacOperonModel model ) {
        super( LacOperonDefaults.VIEW_SIZE );
        
        this.model = model;
        
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
        model.addListener(new LacOperonModel.Listener() {
			public void modelElementAdded(SimpleModelElement modelElement) {
				addModelElement(modelElement);
			}
		});
        
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );
        
        // Layers.
        dnaStrandLayer = new PNode();
        rootNode.addChild(dnaStrandLayer);
        rovingModelElementLayer = new PNode();
        rootNode.addChild(rovingModelElementLayer);
                
        // Add the DNA strand to the canvas.
        dnaStrandLayer.addChild(new DnaStrandNode(model.getDnaStrand(), mvt, getBackground()));
        
        // Add any model elements that are already present in the model.
        for (SimpleModelElement modelElement : model.getAllSimpleModelElements()){
        	addModelElement(modelElement);
        }
        
        // Add the tool box.
        
        addScreenChild(new ToolboxNode(this, model, mvt));
        
    }

    //------------------------------------------------------------------------
    // Accessors
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Canvas Layout
    //------------------------------------------------------------------------
    
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
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
    
    //------------------------------------------------------------------------
    // Other Methods
    //------------------------------------------------------------------------
    
    private void addModelElement(final SimpleModelElement modelElement){
    	final SimpleModelElementNode modelElementNode = new SimpleModelElementNode(modelElement, mvt);
    	
    	if (modelElement.isPartOfDnaStrand()){
    		dnaStrandLayer.addChild(modelElementNode);
    	}
    	else{
    		rovingModelElementLayer.addChild(modelElementNode);
    	}
    	
    	// Register for notification of removal from the model so that the
    	// corresponding node can be removed.
    	modelElement.addListener(new ModelElementListenerAdapter(){
    		@Override
    		public void removedFromModel() {
    	    	if ( rovingModelElementLayer.getChildrenReference().contains(modelElementNode) ){
    	    		rovingModelElementLayer.removeChild(modelElementNode);}
    	    	if ( dnaStrandLayer.getChildrenReference().contains(modelElementNode) ){
    	    		dnaStrandLayer.removeChild(modelElementNode);}
    		}
    	});
    }    
}
