/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas.RenderingSizeStrategy;
import edu.colorado.phet.common.piccolophet.PhetPCanvas.TransformStrategy;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

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

    // Canvas size.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 100;
//    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
//
//    // Translation factors, used to set origin of canvas area.
//    private final double WIDTH_TRANSLATION_FACTOR = 0.5;   // 0 = all the way left, 1 = all the way right.
//    private final double HEIGHT_TRANSLATION_FACTOR = 0.45; // 0 = all the way up, 1 = all the way down.
//
//    // Constants that control relative sizes and placements of major items on
//    // the canvas.
//    private final double BACKGROUND_HEIGHT_PROPORTION = 0.7;     // Vertical fraction of canvas for background.
    private final int INITIAL_INTERMEDIATE_COORD_WIDTH = 1016;
    private final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 593;
    private final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );
    private final double PROPORTIONS_CHART_WIDTH_FRACTION = 0.7; // Fraction of canvas width for proportions chart.
    private final double CHART_HEIGHT = 200; // Chart height in screen coordinates

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ModelViewTransform2D _mvt;
    private RadioactiveDatingGameModel _model;
    private PNode _backgroundImageLayer;
    private PNode _backgroundImage;
//    private PNode _datableArtifactsLayer;
    private NuclearDecayProportionChart _proportionsChart;
    private PPath _testShape;
    private PNode _referenceNode; // For positioning other nodes.

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public RadioactiveDatingGameCanvas(RadioactiveDatingGameModel radioactiveDatingGameModel) {

    	_model = radioactiveDatingGameModel;

    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
        _mvt = new ModelViewTransform2D(new Point2D.Double(0, 0), new Point2D.Double(10, -30),
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, INITIAL_INTERMEDIATE_COORD_HEIGHT / 4),
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH, INITIAL_INTERMEDIATE_COORD_HEIGHT),true);

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
        BufferedImage bufferedImage = NuclearPhysicsResources.getImage( "dating-game-background.png" );
        _backgroundImage = new PImage( bufferedImage );
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
        	addWorldChild(new RadioactiveDatingGameObjectNode(item, _mvt));
        }
        
        // Create the chart that will display relative decay proportions.
        _proportionsChart = new NuclearDecayProportionChart.Builder(Carbon14Nucleus.HALF_LIFE * 3.2,
        		Carbon14Nucleus.HALF_LIFE, NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL,
        		NuclearPhysicsConstants.CARBON_COLOR).
        		postDecayElementLabel(NuclearPhysicsStrings.NITROGEN_14_CHEMICAL_SYMBOL).
        		postDecayLabelColor(NuclearPhysicsConstants.NITROGEN_COLOR).
        		pieChartEnabled(false).
        		showPostDecayCurve(false).
        		timeMarkerLabelEnabled(true).
        		build();
        addWorldChild(_proportionsChart);
        
        // TODO: If I end up leaving this as a world child, I need to change this
        // resizing call to be a part of the constructor for the chart (I think).
        _proportionsChart.componentResized( new Rectangle2D.Double( 0, 0, 
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_CHART_WIDTH_FRACTION,
        		INITIAL_INTERMEDIATE_COORD_HEIGHT - _mvt.modelToViewYDouble(_model.getBottomOfStrata())));
        _proportionsChart.setOffset(
        		INITIAL_INTERMEDIATE_COORD_WIDTH / 2 - _proportionsChart.getFullBoundsReference().width / 2,
        		_mvt.modelToViewYDouble(_model.getBottomOfStrata()));

        // Add decay curve to chart.
        drawDecayCurveOnChart();
        
        // Draw a test shape.  TODO: This should eventually be removed.
        _testShape = new PPath( new Rectangle2D.Double(0, 0, 10, 10));
        _testShape.setStrokePaint(Color.red);
        _testShape.setPaint(Color.red);
        addScreenChild(_testShape);
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
}