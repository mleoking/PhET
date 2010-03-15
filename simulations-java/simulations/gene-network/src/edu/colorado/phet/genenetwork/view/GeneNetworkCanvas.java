/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
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

    // Model-view transform.
    private ModelViewTransform2D mvt;
    
    // Layers for creating the desired overlap behavior.
    private PNode toolBoxLayer;
    private PNode dnaStrandLayer;
    private PNode rovingModelElementLayer1; // Backmost layer for roving elements.
    private PNode rovingModelElementLayer2;
    private PNode rovingModelElementLayer3; // Frontmost layer for roving elements.
    private PNode lactoseInjectorLayer;
    private PNode legendLayer;
    
    // Nodes for which references are needed, generally for layout purposes.
	private MacroMoleculeLegend legend;
	private LactoseMeter lactoseMeter;
    
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
        
        // Layers.
        toolBoxLayer = new PNode();
        addScreenChild(toolBoxLayer);
        dnaStrandLayer = new PNode();
        addWorldChild(dnaStrandLayer);
        rovingModelElementLayer1 = new PNode();
        addWorldChild(rovingModelElementLayer1);
        rovingModelElementLayer2 = new PNode();
        addWorldChild(rovingModelElementLayer2);
        rovingModelElementLayer3 = new PNode();
        addWorldChild(rovingModelElementLayer3);
        lactoseInjectorLayer = new PNode();
        addWorldChild(lactoseInjectorLayer);
        legendLayer = new PNode();
        addScreenChild(legendLayer);
                
        // Add the DNA strand to the canvas.
        dnaStrandLayer.addChild(new DnaStrandNode(model.getDnaStrand(), mvt, getBackground()));

        // Add the cell membrane (if it exists) to the canvas.
        Rectangle2D cellMembraneRect = model.getCellMembraneRect();
        if (cellMembraneRect != null){
        	dnaStrandLayer.addChild(new PhetPPath(mvt.createTransformedShape(cellMembraneRect), Color.ORANGE,
        			new BasicStroke(2f), Color.BLACK));
        }
        
        // Add any model elements that are already present in the model.
        for (SimpleModelElement modelElement : model.getAllSimpleModelElements()){
        	addModelElement(modelElement);
        }
        
        // Add the tool box.
        toolBoxLayer.addChild(new DnaSegmentToolBoxNode(this, model, mvt));
        
        // Add the lactose injector.
        LactoseInjectorNode lactoseInjector = new LactoseInjectorNode(model, mvt);
        lactoseInjector.setOffset(-140, -40);
        lactoseInjectorLayer.addChild(lactoseInjector);
        
        // Add the legend.
        legend = new MacroMoleculeLegend(model, this);
        legendLayer.addChild(legend);
        
        // Add the lactose meter.
        lactoseMeter = new LactoseMeter(model);
        lactoseMeter.setOffset(-140, 250);
        lactoseInjectorLayer.addChild(lactoseMeter);
    }

    //------------------------------------------------------------------------
    // Methods
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
            System.out.println( "cavas worldSize=" + worldSize );
        }
        
        // Place the legend where it needs to go.
        legend.setOffset(getWidth() - legend.getFullBoundsReference().getWidth() - 10, 20);
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
    			rovingModelElementLayer1.addChild(modelElementNode);
    		}
    		else if (modelElement instanceof RnaPolymerase){
    			// Put RNA Polymerase on the foremost layer.
    			rovingModelElementLayer3.addChild(modelElementNode);
    		}
    		else{
    			// Put everything else on the middle layer.
    			rovingModelElementLayer2.addChild(modelElementNode);
    			
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
    	    	if ( rovingModelElementLayer1.getChildrenReference().contains(modelElementNode) ){
    	    		rovingModelElementLayer1.removeChild(modelElementNode);
    	    	}
    	    	else if ( rovingModelElementLayer2.getChildrenReference().contains(modelElementNode) ){
    	    		rovingModelElementLayer2.removeChild(modelElementNode);
    	    	}
    	    	else if ( rovingModelElementLayer3.getChildrenReference().contains(modelElementNode) ){
    	    		rovingModelElementLayer3.removeChild(modelElementNode);
    	    	}
    	    	else if ( dnaStrandLayer.getChildrenReference().contains(modelElementNode) ){
    	    		dnaStrandLayer.removeChild(modelElementNode);
    	    	}
    		}
    	});
    }    
}
