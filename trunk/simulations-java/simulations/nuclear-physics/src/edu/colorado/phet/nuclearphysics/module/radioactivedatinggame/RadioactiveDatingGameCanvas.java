/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
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
    private final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );
    
    // Constants for positioning/size some of the nodes.
    private final double PROPORTIONS_CHART_WIDTH_FRACTION = 0.6; // Fraction of canvas width for proportions chart.
    private final double PROPORTIONS_METER_WIDTH_FRACTION = 0.23; // Fraction of canvas width for proportions chart.

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

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public RadioactiveDatingGameCanvas(RadioactiveDatingGameModel radioactiveDatingGameModel, ProbeTypeModel probeTypeModel) {

    	_model = radioactiveDatingGameModel;

    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
        _mvt = new ModelViewTransform2D(new Point2D.Double(0, 0), 
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, (int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.25)),
        		20, true);

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
        _backgroundImage.setOffset(
        		INITIAL_INTERMEDIATE_COORD_WIDTH / 2 - _backgroundImage.getFullBoundsReference().width / 2, 0);
        _backgroundImageLayer.addChild( _backgroundImage );

        // Add the strata that will contain the datable items.
        ArrayList<Color> colors=new ArrayList<Color>( );
        colors.add( new Color( 111, 131, 151 ) );
        colors.add( new Color( 153, 185, 216 ) );
        colors.add( new Color( 216, 175, 208 ) );
        colors.add( new Color( 198, 218, 119 ) );
        colors.add( new Color( 179, 179, 179 ) );
        for (int i=0;i<_model.getLayerCount();i++){
            addWorldChild(new RadioactiveDatingGameLayerNode(_model.getLayer(i), _mvt,colors.get(i % colors.size())));
        }

        // Add the nodes that represent the items on which the user can
        // perform radiometric dating.
        for (DatableObject item : _model.getItemIterable()){
        	PNode datableItemNode = new DatableItemNode(item, _mvt);
        	datableItemNode.setOffset(_mvt.modelToViewDouble(item.getCenter()));
        	addWorldChild(datableItemNode);
        	
        	// TODO: Temp for debug, should eventually be permanently removed.
//        	PPath boundingNode = new PhetPPath( _mvt.createTransformedShape(item.getBoundingRect()), 
//        			new BasicStroke( 2 ), Color.RED );
//        	addWorldChild(boundingNode);
        }
        
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
        addWorldChild( _meter );
        
        // Add decay curve to chart.
        drawDecayCurveOnChart();
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    protected void updateLayout(){

    	if ( getWidth() > 0 && getHeight() > 0){
    		resizeAndPositionNodes( getWidth(), getHeight() );
    	}
    }

    /**
     * Resize and position the nodes that need to have this done explicitly,
     * as opposed to the nodes where the canvas takes care of it for us.
     * 
     * @param newWidth
     * @param newHeight
     */
    private void resizeAndPositionNodes( double newWidth, double newHeight ){

    	// TODO: I (jblanco) started down a path where some nodes were being
    	// explicitly repositioned when the window was resized, but then
    	// created a new transform in the canvas that seems (at least
    	// initially) to do what is needed automatically.  So, this method
    	// can probably go at some point, but keep it for a while just in case
    	// the whole transform thing doesn't work out.
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