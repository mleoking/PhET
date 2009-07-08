/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Alpha Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class RadiometricMeasurementCanvas extends PhetPCanvas {

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
    private static final double PROPORTIONS_CHART_WIDTH_FRACTION = 0.75;
    
    // Proportions and location of the meter on the canvas.
    private static final double PROPORTIONS_METER_WIDTH_FRACTION = 0.23;
    private static final double PROPORTIONS_METER_HEIGHT_FRACTION = 0.25;
    private static final double METER_X_OFFSET = 70;
    
    // Resolution of the decay chart.
    private static final double NUM_SAMPLES_ON_DECAY_CHART = 250;
    
    // Fixed distance from very top of canvas where the meter and chart will
    // be positioned.
    private static final double METER_AND_CHART_OFFSET_FROM_TOP = 8;
    
    // Constants that control the appearance of the buttons in the play area.
    private static final Color START_OPERATION_BUTTON_COLOR = new Color(255, 204, 102);
    private static final Color FORCE_CLOSURE_BUTTON_COLOR = new Color(250, 70, 0);
    private static final int PLAY_AREA_BUTTON_FONT_SIZE = 24;
    private static final double BUTTON_DISTANCE_FROM_BOTTOM = 20;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ModelViewTransform2D _mvt;
    private RadiometricMeasurementModel _model;
    private PNode _datableItemsLayer;
    private PNode _chartAndMeterLayer;
    private NuclearDecayProportionChart _proportionsChart;
    private RadiometricDatingMeterNode _meterNode;
    private IdentityHashMap<DatableItem, PNode> _mapModelElementsToNodes = new IdentityHashMap<DatableItem, PNode>();
    private GradientButtonNode _startOperationButtonNode;
    private GradientButtonNode _forceClosureButtonNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    RadiometricMeasurementCanvas(RadiometricMeasurementModel radiometricMeasurementModel) {

    	_model = radiometricMeasurementModel;

    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
        _mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.7)),
        		20,
        		true);
        
        // Register with the meter in the model in order to know when the user
        // changes any of the meter settings, since they need to be propagated
        // to the chart.
        _model.getMeter().addListener(new RadiometricDatingMeter.Adapter(){
        	public void datingElementChanged(){
        		configureProportionsChart();
        		update();
        	};
        	public void touchedStateChanged(){
        		handleMeterTouchStateChanged();
        	};
        });
        
        // Register with the model for notifications of new elements coming
        // and going.
        _model.addListener(new RadiometricMeasurementModel.Adapter(){
    		public void modelElementAdded() {
    			handleModelElementAdded();
    		}
    		public void modelElementRemoved() {
    			handleModelElementRemoved();
    		}
    		public void simulationModeChanged() {
    			handleSimulationModeChanged();
    		}
    		public void closureStateChanged() {
    			updateButtonState();
    		}
        });
        
        // Register with the model's clock for changes in status that impact
        // what is presented to the user.
        _model.getClock().addClockListener(new ClockAdapter(){
            public void clockStarted( ClockEvent clockEvent ) {
            	updateButtonState();
            }
        });
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );

        // Create the layer where the background will be placed.
        PNode backgroundLayer = new PNode();
        addWorldChild(backgroundLayer);
        
        // Create the layer where the datable items will be placed.
        _datableItemsLayer = new PNode();
        addWorldChild(_datableItemsLayer);
        
        _chartAndMeterLayer = new PNode();
        addWorldChild(_chartAndMeterLayer);
        
        // Add the ground to the background.
        GroundNode ground = new GroundNode();
        ground.setOffset(_mvt.modelToViewXDouble(0), _mvt.modelToViewYDouble(_model.getGroundLevelY()));
        backgroundLayer.addChild(ground);

        // Add the sky to the background.
        SkyNode sky = new SkyNode(INITIAL_INTERMEDIATE_COORD_WIDTH * 4, INITIAL_INTERMEDIATE_COORD_HEIGHT * 1.5);
        sky.setOffset(_mvt.modelToViewXDouble(0), _mvt.modelToViewYDouble(_model.getGroundLevelY()));
        backgroundLayer.addChild(sky);
        
        // Add a couple of clouds to the background.
        PImage cloud1 = NuclearPhysicsResources.getImageNode("cloud_1.png");
        cloud1.setScale(0.75);
        cloud1.setOffset(50, 250);
        backgroundLayer.addChild(cloud1);
        PImage cloud2 = NuclearPhysicsResources.getImageNode("cloud_1.png");
        cloud2.setScale(0.5);
        cloud2.setOffset(720, 350);
        backgroundLayer.addChild(cloud2);
        
        // Create the chart that will display relative decay proportions.
        _proportionsChart = new NuclearDecayProportionChart(false, false, false);
        configureProportionsChart();
        _chartAndMeterLayer.addChild(_proportionsChart);
        
        // Set the size and position of the chart.  There are some "tweak
        // factors" in here to make things look good.
        // TODO: If I end up leaving this as a world child, I need to change this
        // resizing call to be a part of the constructor for the chart (I think).
        _proportionsChart.componentResized( new Rectangle2D.Double( 0, 0, 
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_CHART_WIDTH_FRACTION,
        		INITIAL_INTERMEDIATE_COORD_HEIGHT * PROPORTIONS_METER_HEIGHT_FRACTION));
        _proportionsChart.setOffset(
        		METER_X_OFFSET + INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_METER_WIDTH_FRACTION + 8,
        		METER_AND_CHART_OFFSET_FROM_TOP);
        
        // Create the radiometric measuring device.
        _meterNode = new RadiometricDatingMeterNode( _model.getMeter(), INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_METER_WIDTH_FRACTION,
        		INITIAL_INTERMEDIATE_COORD_HEIGHT * PROPORTIONS_METER_HEIGHT_FRACTION, 
        		_mvt,
        		this,
        		false );
        _meterNode.setMeterBodyOffset( METER_X_OFFSET, METER_AND_CHART_OFFSET_FROM_TOP);
        _meterNode.enablePeriodicUpdate(true);
        _chartAndMeterLayer.addChild( _meterNode );

        // Set up the initial buttons in the play area.
        updateButtonState();
        
        // Add the node(s) to the canvas corresponding to the datable items in
        // the model.
        handleModelElementAdded();
    }

	//------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * Handle a notification that a model element was added by comparing the
     * list of elements in the model to the list of corresponding nodes and
     * creating new nodes for any elements that are not yet represented.
     */
    private void handleModelElementAdded(){
    	
    	ArrayList modelElements = _model.getModelElements();
    	
    	for (Object modelElement : modelElements){
    		if (_mapModelElementsToNodes.get(modelElement) == null){
    			// No node exists for this model element, so add one.
    			if (modelElement instanceof AnimatedDatableItem){
    				// Add a new node for this model element.
    				DatableItem item = (AnimatedDatableItem)modelElement;
    				DatableItemNode itemNode = new DatableItemNode(item, _mvt);
    				_mapModelElementsToNodes.put(item, itemNode);
    				_datableItemsLayer.addChild(itemNode);
    			}
    			else{
    				// TODO: Not sure if there is a need for handling of items
    				// other than non-datable items.  The volcano may, for
    				// instance, spew out non-datable stuff.  If so, this will
    				// need to be handled at some point.
    				assert false;
    			}
    			
    		}
    	}
    }
    
    /**
     * Handle and event that signifies that a model element has been removed
     * from the model by removing the corresponding node from the canvas.
     */
    private void handleModelElementRemoved(){
    	
    	ArrayList _modelElements = _model.getModelElements();
    	Iterator itr = _mapModelElementsToNodes.values().iterator();
    	
    	while ( itr.hasNext() ){
    		PNode itemNode = (PNode)itr.next();
    		if (!_modelElements.contains(itemNode)){
    			// The element that corresponds to this node appears to have
    			// been removed from the model, so it should be removed from
    			// the canvas and the mapping structure.
    			_datableItemsLayer.removeChild(itemNode);
    			itr.remove();
    		}
    	}
    }

    /**
     * Handle a notification that the simulation mode - which means whether
     * a tree, a rock, or whatever is being simulated - has changed.
     */
    private void handleSimulationModeChanged(){
    	updateButtonState();
    }
    
    /**
     * Update the state of the play area buttons based on the current state of
     * the model.
     */
    private void updateButtonState(){
    	
    	// Remove any pre-existing buttons.
    	if (_startOperationButtonNode != null){
    		_chartAndMeterLayer.removeChild(_startOperationButtonNode);
    		_startOperationButtonNode = null;
    	}
    	if (_forceClosureButtonNode != null){
    		_chartAndMeterLayer.removeChild(_forceClosureButtonNode);
    		_forceClosureButtonNode = null;
    	}
    	
    	// If the clock is not running, set up the buttons to initiate the
    	// simulation.
    	if ( !_model.getClock().isRunning() ){
    		switch ( _model.getSimulationMode() ){
    		
    		case TREE:
    			_startOperationButtonNode = new GradientButtonNode(NuclearPhysicsStrings.PLANT_TREE, 
    					PLAY_AREA_BUTTON_FONT_SIZE, START_OPERATION_BUTTON_COLOR); 
    			break;
    		case ROCK:
    			_startOperationButtonNode = new GradientButtonNode(NuclearPhysicsStrings.ERUPT_VOLCANO, 
    					PLAY_AREA_BUTTON_FONT_SIZE, START_OPERATION_BUTTON_COLOR); 
    		}
    		
    		_startOperationButtonNode.setOffset(
   				INITIAL_INTERMEDIATE_COORD_WIDTH - _startOperationButtonNode.getFullBoundsReference().width,
   				INITIAL_INTERMEDIATE_COORD_HEIGHT - BUTTON_DISTANCE_FROM_BOTTOM - _startOperationButtonNode.getFullBoundsReference().height);
    		_chartAndMeterLayer.addChild(_startOperationButtonNode);
    		
    		// Hook up the button to the method in the model that will start
    		// the simulation.
    		_startOperationButtonNode.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					_model.startOperation();
				}
    		});
    	}
    	else {
    		// Clock is running, so if closure has not occurred we need to add
    		// the button to force it.
    		if (_model.getRadiometricClosureState() == RadiometricClosureState.CLOSURE_POSSIBLE){
    			switch ( _model.getSimulationMode() ){
    			
    			case TREE:
    				_forceClosureButtonNode = new GradientButtonNode(NuclearPhysicsStrings.KILL_TREE, 
    						PLAY_AREA_BUTTON_FONT_SIZE, FORCE_CLOSURE_BUTTON_COLOR); 
    				break;
    			case ROCK:
    				_forceClosureButtonNode = new GradientButtonNode(NuclearPhysicsStrings.COOL_ROCK, 
    						PLAY_AREA_BUTTON_FONT_SIZE, FORCE_CLOSURE_BUTTON_COLOR);
    				break;
    			}

    			_forceClosureButtonNode.setOffset(
           				INITIAL_INTERMEDIATE_COORD_WIDTH - _forceClosureButtonNode.getFullBoundsReference().width,
           				INITIAL_INTERMEDIATE_COORD_HEIGHT - BUTTON_DISTANCE_FROM_BOTTOM - _forceClosureButtonNode.getFullBoundsReference().height);
            	_chartAndMeterLayer.addChild(_forceClosureButtonNode);
            		
        		// Hook up the button to the method in the model that will start
        		// the simulation.
        		_forceClosureButtonNode.addActionListener(new ActionListener(){
    				public void actionPerformed(ActionEvent e) {
    					_model.forceClosure();
    				}
        		});
    		}
    	}
    }
    
    /**
     * Handle a notification from the meter that indicates that it has started
     * or stopped touching a datable item.
     */
    private void handleMeterTouchStateChanged(){
    	
    	DatableItem itemBeingTouched = _model.getMeter().getItemBeingTouched();
    	
    	if (itemBeingTouched != null){
        	// TODO
    	}
    	else {
    	}
    }
    
    private void configureProportionsChart(){
    	
        double halfLife = _model.getMeter().getHalfLifeForDating();
        _proportionsChart.setTimeParameters(halfLife * 3.2, halfLife);
        _proportionsChart.setDisplayInfoForNucleusType(_model.getMeter().getNucleusTypeUsedForDating());
    }
    
    /**
     * Class used to represent the ground on this canvas.
     */
    private static class GroundNode extends PNode {

    	public static final double GROUND_WIDTH = INITIAL_INTERMEDIATE_COORD_WIDTH * 4;
    	public static final double GROUND_HEIGHT = INITIAL_INTERMEDIATE_COORD_HEIGHT * 2;
		public GroundNode() {
			PPath ground = new PPath( new Rectangle2D.Double( 0, 0, GROUND_WIDTH, GROUND_HEIGHT));
			GradientPaint groundGradient = new GradientPaint(0, 0, new Color(0, 100, 0), 0, (float)GROUND_HEIGHT / 4, 
					new Color(120, 255, 60), false);
			ground.setPaint(groundGradient);
			ground.setOffset(-GROUND_WIDTH / 2, 0);
			addChild(ground);
		}
    }
}