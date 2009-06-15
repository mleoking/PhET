/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.IdentityHashMap;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Alpha Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	// Initial size of the reference coordinates that are used when setting up
	// the canvas transform strategy.  These were empirically determined to
	// match the expected initial size of the canvas.
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private static final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );
    
    // Fraction of canvas width used for the proportions chart.
    private static final double PROPORTIONS_CHART_WIDTH_FRACTION = 0.6;
    
    // Fraction of canvas width for the meter.
    private static final double PROPORTIONS_METER_WIDTH_FRACTION = 0.23;
    
    // Fraction of canvas width used to portray the edge of the world.
    private static final double WORLD_EDGE_WIDTH_PROPORTION = 0.05;       
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ModelViewTransform2D _mvt;
    private RadioactiveDatingGameModel _model;
    private PNode _backgroundImageLayer;
    private PNode _backgroundImage;
    private PNode _strataLayer;
    private PNode _guessingGameLayer;
    private NuclearDecayProportionChart _proportionsChart;
    private RadiometricDatingMeterNode _meter;
    private PNode _referenceNode; // For positioning other nodes.
    private ArrayList<StratumNode> _stratumNodes = new ArrayList<StratumNode>();
    private EdgeOfWorldNode _edgeOfWorld;
    private AgeGuessingNode _ageGuessingNode;
    private IdentityHashMap<DatableItem, PNode> _mapDatableItemsToNodes = 
    	new IdentityHashMap<DatableItem, PNode>();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public RadioactiveDatingGameCanvas(RadioactiveDatingGameModel radioactiveDatingGameModel, 
    		ProbeTypeModel probeTypeModel) {

    	_model = radioactiveDatingGameModel;

    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
        _mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.25)),
        		20,
        		true);
        
        // Register with the meter in the model in order to know when the user
        // changes any of the meter settings, since they need to be propagated
        // to the chart.
        _model.getMeter().addListener(new RadiometricDatingMeter.Adapter(){
        	public void datingElementChanged(){
        		configureProportionsChart();
        		drawDecayCurveOnChart();
        		update();
        	};
        });
        
        // Register with the meter so that we know when the user touches or
        // stops touching something.
        _model.getMeter().addListener(new RadiometricDatingMeter.Adapter(){
        	public void touchedStateChanged(){
        		handleMeterTouchStateChanged();
        	};
        });

        // Add a reference node that will be used when positioning other nodes later.
        _referenceNode = new PNode();
        addWorldChild(_referenceNode);
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );

        // Create the layer where the background will be placed.
        _backgroundImageLayer = new PNode();
        addWorldChild(_backgroundImageLayer);

        // Create the layer where the strata and the datable items will be
        // located.
        _strataLayer = new PNode();
        addWorldChild(_strataLayer);

        // Create the layer where the data entry and result nodes for the
        // guessing game will reside.
        _guessingGameLayer = new PNode();
        addWorldChild(_guessingGameLayer);

        // Load the background image.
        BufferedImage bufferedImage = NuclearPhysicsResources.getImage( "green-hills-and-sky.png" );
        _backgroundImage = new PImage( bufferedImage );
        _backgroundImage.scale(1.2);  // Empirically determined scaling factor, tweak as needed.
        _backgroundImage.setOffset(
        		INITIAL_INTERMEDIATE_COORD_WIDTH / 2 - _backgroundImage.getFullBoundsReference().width / 2, 0);
        _backgroundImageLayer.addChild( _backgroundImage );

        // Add the strata that will contain the datable items.
        for (int i=0; i<_model.getLayerCount(); i++){
        	StratumNode stratumNode = new StratumNode( _model.getLayer(i), 
        			NuclearPhysicsConstants.strataColors.get(i % NuclearPhysicsConstants.strataColors.size()),
        			_mvt );
        	_stratumNodes.add(stratumNode);
            _strataLayer.addChild(stratumNode);
        }
        
        // Add the nodes that represent the items on which the user can
        // perform radiometric dating.
        for (DatableItem item : _model.getItemIterable()){
        	PNode datableItemNode = new DatableItemNode(item, _mvt);
        	_mapDatableItemsToNodes.put(item, datableItemNode);
        	datableItemNode.setOffset(_mvt.modelToViewDouble(item.getCenter()));
        	_strataLayer.addChild(datableItemNode);
        }
        	
        // Add the node that represents the edge of the world.
        _edgeOfWorld = new EdgeOfWorldNode(_model, _mvt);
        addWorldChild(_edgeOfWorld);
        
        // Create the chart that will display relative decay proportions.
        _proportionsChart = new NuclearDecayProportionChart(false, true, false);
        configureProportionsChart();
        addWorldChild(_proportionsChart);
        
        // Set the size and position of the chart.  There are some "tweak
        // factors" in here to make things look good.
        // TODO: If I end up leaving this as a world child, I need to change this
        // resizing call to be a part of the constructor for the chart (I think).
        _proportionsChart.componentResized( new Rectangle2D.Double( 0, 0, 
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_CHART_WIDTH_FRACTION,
        		INITIAL_INTERMEDIATE_COORD_HEIGHT - _mvt.modelToViewYDouble(_model.getBottomOfStrata()) - 12));
        _proportionsChart.setOffset(
        		INITIAL_INTERMEDIATE_COORD_WIDTH * 0.6 - _proportionsChart.getFullBoundsReference().width / 2,
        		_mvt.modelToViewYDouble(_model.getBottomOfStrata()) + 4);

        // Create the radiometric measuring device.
        _meter = new RadiometricDatingMeterNode(_model.getMeter(), probeTypeModel,
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_METER_WIDTH_FRACTION, (INITIAL_INTERMEDIATE_COORD_HEIGHT - _mvt.modelToViewYDouble(_model.getBottomOfStrata())) * 0.95, _mvt );
        _meter.setMeterBodyOffset(
        		_proportionsChart.getFullBoundsReference().getMinX() - (_meter.getMeterBodyWidth() * 1.05),
        		_mvt.modelToViewYDouble(_model.getBottomOfStrata()) + 4);
        setUpComboBox();
        addWorldChild( _meter );
        
        // Draw the decay curve on the chart.
        drawDecayCurveOnChart();
    }

    //Workaround to get PComboBox to show popup in the right spot.
    private void setUpComboBox() {
		_meter.getComboBox().setEnvironment(_meter.getComboBoxPSwing(), this);
	}

	//------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * Update the layout of the nodes on this canvas.  This handles
     * specialized node movement and resizing that can't be fully handled
     * by the canvas transform strategy.
     */
    public void updateLayout(){

    	if ( getWidth() > 0 && getHeight() > 0){
	    	// Set the location of the edge of the world.  This is done here
    		// because the edge changes location and size based on the size
    		// of the viewport into the world.  IMPORTANT: Currently, this
    		// positions the edge on the LEFT SIDE of the viewport.
    		
   	    	Point2D innerEdgeOfWorldScreen = new Point2D.Double(getWidth() * WORLD_EDGE_WIDTH_PROPORTION, 0);
	    	Point2D outerEdgeOfWorldScreen = new Point2D.Double(0, 0);
	    	Point2D innerEdgeOfWorldIntermediate = new Point2D.Double();
	    	Point2D outerEdgeOfWorldIntermediate = new Point2D.Double();
	    	AffineTransform intermediateToScreenTransform = getWorldTransformStrategy().getTransform();
	    	try {
				intermediateToScreenTransform.inverseTransform(innerEdgeOfWorldScreen,
						innerEdgeOfWorldIntermediate);
				intermediateToScreenTransform.inverseTransform(outerEdgeOfWorldScreen,
						outerEdgeOfWorldIntermediate);
			} catch (NoninvertibleTransformException e) {
				System.err.println("Error: Unable to invert transform.");
				e.printStackTrace();
				assert false;
			}
			
    		_edgeOfWorld.updateEdgeShape(innerEdgeOfWorldIntermediate.getX(), outerEdgeOfWorldIntermediate.getX());
    	}
    }
    
    private void drawDecayCurveOnChart(){
        double halfLife = _model.getMeter().getHalfLifeForDating();
    	final int numSamples = 100;
    	_proportionsChart.clear();
    	double timeSpan = halfLife * 3;
    	double timeIncrement = timeSpan / numSamples;
    	double lambda = Math.log(2)/halfLife;
    	for ( double time = 0; time < timeSpan; time += timeIncrement ){
    		// Calculate the proportion of the element that should be decayed at this point in time.
    		double amountDecayed = numSamples - (numSamples * Math.exp(-time*lambda));
    		_proportionsChart.addDataPoint(time, (int)Math.round(numSamples - amountDecayed), 
    				(int)Math.round(amountDecayed));
    	}
    }
    
    /**
     * Handle a notification from the meter that indicates that it has started
     * or stopped touching a datable item.
     */
    private void handleMeterTouchStateChanged(){
    	
    	// Clean up any existing nodes.
    	if (_ageGuessingNode != null){
    		_guessingGameLayer.removeChild(_ageGuessingNode);
    		_ageGuessingNode = null;
    	}
    	
    	DatableItem itemBeingTouched = _model.getMeter().getItemBeingTouched();
    	if (itemBeingTouched != null){
    		
    		// Create a new guessing box.
    		_ageGuessingNode = new AgeGuessingNode();
    		// TODO: How do I mark the nodes that have already been guessed?  Probably need a map.
    		
    		// Position the guessing box to the side of the node that
    		// represents the item being dated.
    		PNode datableItemNode = _mapDatableItemsToNodes.get(itemBeingTouched);
    		Point2D ageGuessingNodeLocation = new Point2D.Double(0, 0);
    		if (datableItemNode == null){
    			System.err.println(getClass().getName() + " - Error: Could not locate node for datable item " + itemBeingTouched);
    			assert false;
    		}
    		else{
    			// Position the guessing box to the side of the node that
    			// represents the item being touched.  There is a tweak factor
    			// in here to add a little space between the guessing box and
    			// the datable item node.
    			ageGuessingNodeLocation.setLocation(
    					datableItemNode.getFullBoundsReference().getMaxX() + 8,
    					datableItemNode.getFullBoundsReference().getCenterY() - _ageGuessingNode.getFullBoundsReference().height / 2);
    		}
    			
    		_ageGuessingNode.setOffset(ageGuessingNodeLocation);
    		_guessingGameLayer.addChild(_ageGuessingNode);
    	}
    }

    private void configureProportionsChart(){
    	
        double halfLife = _model.getMeter().getHalfLifeForDating();
        _proportionsChart.setTimeParameters(halfLife * 3.2, halfLife);
        _proportionsChart.setDisplayInfoForNucleusType(_model.getMeter().getNucleusTypeUsedForDating());
    }
}