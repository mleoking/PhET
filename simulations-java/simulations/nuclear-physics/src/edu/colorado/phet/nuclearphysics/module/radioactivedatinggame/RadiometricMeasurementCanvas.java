/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
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
    private static final double PROPORTIONS_CHART_WIDTH_FRACTION = 0.9;
    
    // Proportions of the meter on the canvas.
    private static final double PROPORTIONS_METER_WIDTH_FRACTION = 0.23;
    private static final double PROPORTIONS_METER_HEIGHT_FRACTION = 0.25;
    
    // Resolution of the decay chart.
    private static final double NUM_SAMPLES_ON_DECAY_CHART = 250;
    
    // Fixed distance from very top of canvas where the meter and chart will
    // be positioned.
    private static final double METER_AND_CHART_OFFSET_FROM_TOP = 8;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ModelViewTransform2D _mvt;
    private RadiometricMeasurementModel _model;
    private PNode _backgroundLayer;
    private NuclearDecayProportionChart _proportionsChart;
    private RadiometricDatingMeterNode _meterNode;
    private IdentityHashMap<DatableItem, PNode> _mapModelElementsToNodes = new IdentityHashMap<DatableItem, PNode>();

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

        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );

        // Create the layer where the background will be placed.
        _backgroundLayer = new PNode();
        addWorldChild(_backgroundLayer);
        
        // Add the ground to the background.
        GroundNode ground = new GroundNode();
        ground.setOffset(_mvt.modelToViewXDouble(0), _mvt.modelToViewYDouble(_model.getGroundLevelY()));
        _backgroundLayer.addChild(ground);

        // Add the sky to the background.
        SkyNode sky = new SkyNode(INITIAL_INTERMEDIATE_COORD_WIDTH * 4, INITIAL_INTERMEDIATE_COORD_HEIGHT * 1.5);
        sky.setOffset(_mvt.modelToViewXDouble(0), _mvt.modelToViewYDouble(_model.getGroundLevelY()));
        _backgroundLayer.addChild(sky);
        
        // Add a couple of clouds to the background.
        PImage cloud1 = NuclearPhysicsResources.getImageNode("cloud_1.png");
        cloud1.setScale(0.75);
        cloud1.setOffset(0, 20);
        _backgroundLayer.addChild(cloud1);
        PImage cloud2 = NuclearPhysicsResources.getImageNode("cloud_1.png");
        cloud2.setScale(0.5);
        cloud2.setOffset(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 100);
        _backgroundLayer.addChild(cloud2);
        
        // Add the volcano.
        // TODO: This should be in the model, and is here now solely for demo/testing purposes.
        PNode volcano = NuclearPhysicsResources.getImageNode("volcano_hot.png");
        volcano.scale(0.75);
        volcano.setOffset(700, 330);
        _backgroundLayer.addChild(volcano);

        // Add the nodes that represent the items on which the user can
        // perform radiometric dating.
        	
        // Create the radiometric measuring device.
        _meterNode = new RadiometricDatingMeterNode( _model.getMeter(), INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_METER_WIDTH_FRACTION,
        		INITIAL_INTERMEDIATE_COORD_HEIGHT * PROPORTIONS_METER_HEIGHT_FRACTION, 
        		_mvt,
        		this,
        		false );
        _meterNode.setMeterBodyOffset( 0, METER_AND_CHART_OFFSET_FROM_TOP);
        addWorldChild( _meterNode );
        
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
        		_meterNode.getMeterBodySize().getHeight()));
        _proportionsChart.setOffset(
        		_meterNode.getMeterBodyOffset().getX() + _meterNode.getMeterBodySize().getWidth() + 4,
        		METER_AND_CHART_OFFSET_FROM_TOP);

        // Draw the decay curve on the chart.
        drawDecayCurveOnChart();
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
    			if (modelElement instanceof DatableItem){
    				DatableItem item = (DatableItem)modelElement;
    				DatableItemNode itemNode = new DatableItemNode(item, _mvt);
    				_mapModelElementsToNodes.put(item, itemNode);
    				addWorldChild(itemNode);
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
    			removeWorldChild(itemNode);
    			itr.remove();
    		}
    	}
    }
    
    private void drawDecayCurveOnChart(){
        double halfLife = _model.getMeter().getHalfLifeForDating();
    	_proportionsChart.clear();
    	double timeSpan = halfLife * 3.2;
    	double timeIncrement = timeSpan / NUM_SAMPLES_ON_DECAY_CHART;
    	double lambda = Math.log(2)/halfLife;
    	for ( double time = 0; time < timeSpan; time += timeIncrement ){
    		// Calculate the proportion of the element that should be decayed at this point in time.
    		double amountDecayed = NUM_SAMPLES_ON_DECAY_CHART - (NUM_SAMPLES_ON_DECAY_CHART * Math.exp(-time*lambda));
    		_proportionsChart.addDataPoint(time, (int)Math.round(NUM_SAMPLES_ON_DECAY_CHART - amountDecayed), 
    				(int)Math.round(amountDecayed));
    	}
    	_proportionsChart.updateMarkerText();
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