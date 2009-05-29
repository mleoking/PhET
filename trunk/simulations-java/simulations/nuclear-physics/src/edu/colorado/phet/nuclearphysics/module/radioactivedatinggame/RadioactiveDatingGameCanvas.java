/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
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
    
    // Fraction of canvas height used for the additional height of the edge
    // of the world ABOVE AND BEYOND the depth of the strata.
    private static final double WORLD_EDGE_ADDITIONAL_HEIGHT_PROPORTION = 0.21;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ModelViewTransform2D _mvt;
    private RadioactiveDatingGameModel _model;
    private PNode _backgroundImageLayer;
    private PNode _backgroundImage;
//    private PNode _datableItemsLayer;
    private NuclearDecayProportionChart _proportionsChart;
    private RadiometricDatingMeterNode _meter;
    private PNode _referenceNode; // For positioning other nodes.
    private ArrayList<StratumNode> _stratumNodes = new ArrayList<StratumNode>();
    private EdgeOfWorldNode _edgeOfWorld;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public RadioactiveDatingGameCanvas(RadioactiveDatingGameModel radioactiveDatingGameModel, ProbeTypeModel probeTypeModel) {

    	_model = radioactiveDatingGameModel;

    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
        _mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.27)),
        		20,
        		true);

        // Add a reference node that will be used when positioning other nodes later.
        _referenceNode = new PNode();
        addWorldChild(_referenceNode);
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );

        // Create the layer where the background will be placed.
        _backgroundImageLayer = new PNode();
        addWorldChild(_backgroundImageLayer);

        // Create the layer where the datable items will be located.
//        _datableArtifactsLayer = new PNode();
//        addScreenChild(_datableArtifactsLayer);

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
            addWorldChild(stratumNode);
        }

        // Add the nodes that represent the items on which the user can
        // perform radiometric dating.
        for (DatableObject item : _model.getItemIterable()){
        	PNode datableItemNode = new DatableItemNode(item, _mvt);
        	datableItemNode.setOffset(_mvt.modelToViewDouble(item.getCenter()));
        	addWorldChild(datableItemNode);
        }
        	
        // Add the node that represents the edge of the world.
        _edgeOfWorld = new EdgeOfWorldNode(_model, _mvt);
        addWorldChild(_edgeOfWorld);
        
        // Create the chart that will display relative decay proportions.
        _proportionsChart = new NuclearDecayProportionChart(false);
        configureProportionsChart();
        addWorldChild(_proportionsChart);
        
        // TODO: If I end up leaving this as a world child, I need to change this
        // resizing call to be a part of the constructor for the chart (I think).
        _proportionsChart.componentResized( new Rectangle2D.Double( 0, 0, 
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_CHART_WIDTH_FRACTION,
        		INITIAL_INTERMEDIATE_COORD_HEIGHT - _mvt.modelToViewYDouble(_model.getBottomOfStrata())));
        _proportionsChart.setOffset(
        		INITIAL_INTERMEDIATE_COORD_WIDTH * 0.6 - _proportionsChart.getFullBoundsReference().width / 2,
        		_mvt.modelToViewYDouble(_model.getBottomOfStrata()));

        // Create the radiometric measuring device.
        _meter = new RadiometricDatingMeterNode(_model.getMeter(), probeTypeModel,
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_METER_WIDTH_FRACTION, (INITIAL_INTERMEDIATE_COORD_HEIGHT - _mvt.modelToViewYDouble(_model.getBottomOfStrata())) * 0.95, _mvt );
        _meter.setMeterBodyOffset(
        		_proportionsChart.getFullBoundsReference().getMinX() - (_meter.getMeterBodyWidth() * 1.05),
        		_mvt.modelToViewYDouble(_model.getBottomOfStrata()) + 4);
        setUpComboBox();
        addWorldChild( _meter );
        
        // Add decay curve to chart.
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
    protected void updateLayout(){

    	if ( getWidth() > 0 && getHeight() > 0){
	    	// Set the location of the edge of the world in the model.  This
    		// is done here because the edge changes location and size based
    		// on the size of the viewport into the world.  Changing it in
    		// the model causes the information to be propagated to any nodes
    		// that need to be aware of it.  IMPORTANT: Currently, this
    		// positions the edge on the LEFT SIDE of the viewport.
    		
    		// The variables that must be assigned values in order to set the
    		// edge rectangle in the model.
    		double edgeOfWorldXModel, edgeOfWorldYModelY, edgeOfWorldWidthModel, edgeOfWorldHeightModel;
    		
    		// The Y values are relatively easy, so do them first.
    		edgeOfWorldYModelY = _model.getBottomOfStrata();
    		edgeOfWorldHeightModel = -(_model.getBottomOfStrata() * (1 + WORLD_EDGE_ADDITIONAL_HEIGHT_PROPORTION));
    		
    		// The x values must be transformed from screen to intermediate to
    		// model coordinates.
	    	AffineTransform intermediateToScreenTransform = getWorldTransformStrategy().getTransform();
	    	Point2D innerEdgeOfWorldXScreen = new Point2D.Double(getWidth() * WORLD_EDGE_WIDTH_PROPORTION, 0);
	    	Point2D innerEdgeOfWorldXIntermediate = new Point2D.Double();
	    	Point2D outerEdgeOfWorldXScreen = new Point2D.Double(0, 0);
	    	Point2D outerEdgeOfWorldXIntermediate = new Point2D.Double();
	    	try {
				intermediateToScreenTransform.inverseTransform(innerEdgeOfWorldXScreen,
						innerEdgeOfWorldXIntermediate);
				intermediateToScreenTransform.inverseTransform(outerEdgeOfWorldXScreen,
						outerEdgeOfWorldXIntermediate);
			} catch (NoninvertibleTransformException e) {
				System.err.println("Error: Unable to invert transform.");
				e.printStackTrace();
				assert false;
			}
			
			edgeOfWorldXModel = _mvt.viewToModelX(outerEdgeOfWorldXIntermediate.getX());
			edgeOfWorldWidthModel = _mvt.viewToModelDifferentialX(innerEdgeOfWorldXIntermediate.getX() 
					- outerEdgeOfWorldXIntermediate.getX());
		
    		_model.setEdgeOfWorldRect(new Rectangle2D.Double(edgeOfWorldXModel, edgeOfWorldYModelY, 
    				edgeOfWorldWidthModel, edgeOfWorldHeightModel));
    		_edgeOfWorld.updateEdgeShape(innerEdgeOfWorldXIntermediate.getX(), outerEdgeOfWorldXIntermediate.getX());
//    		_model.setEdgeOfWorldRect(new Rectangle2D.Double(-25, _model.getBottomOfStrata(), 
//    				5, 20));
    	}
    }

    /**
     * Set up the chart to show the appropriate decay curve.
     */
    private void drawDecayCurveOnChart(){
    	_proportionsChart.clear();
    	double timeSpan = Carbon14Nucleus.HALF_LIFE * 3;
    	double timeIncrement = timeSpan / 500;
    	double lambda = Math.log(2)/Carbon14Nucleus.HALF_LIFE;
    	for ( double time = 0; time < timeSpan; time += timeIncrement ){
    		// Calculate the proportion of carbon that should be decayed at this point in time.
    		double percentageDecayed = 100 - (100 * Math.exp(-time*lambda));
    		_proportionsChart.addDecayEvent(time, percentageDecayed);
    	}
    }
    
    private void configureProportionsChart(){
        _proportionsChart.setTimeSpan(Carbon14Nucleus.HALF_LIFE * 3.2);
        _proportionsChart.setHalfLife(Carbon14Nucleus.HALF_LIFE);
        _proportionsChart.setPreDecayElementLabel(NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL);
        _proportionsChart.setPreDecayLabelColor(NuclearPhysicsConstants.CARBON_COLOR);
        _proportionsChart.setShowPostDecayCurve(true);
        _proportionsChart.setTimeMarkerLabelEnabled(false);
    }
}