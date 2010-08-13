/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.audio.AudioResourcePlayer;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;
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
    private static final double PROPORTIONS_CHART_WIDTH_FRACTION = 0.9;
    
    // Fraction of canvas width for the meter.
    private static final double PROPORTIONS_METER_WIDTH_FRACTION = 0.23;
    
    // Fraction of canvas width for the meter.
    private static final double PROPORTIONS_METER_AND_CHART_HEIGHT_FRACTION = 0.32;
    
    // Fraction of canvas width used to portray the edge of the world.
    private static final double WORLD_EDGE_WIDTH_PROPORTION = 0.05;  
    
    // Constant that controls how close the user must be to the actual age
    // of an item to be considered correct.  This is a percentage, and a
    // value of 0 means the user must be perfectly accurate and a value of 100
    // means that they can be off by as much as the value of the age.
    private static final double AGE_GUESS_TOLERANCE_PERCENTAGE = 20;
    
    // Resolution of the decay chart.
    private static final double NUM_SAMPLES_ON_DECAY_CHART = 500;
    
    // Constant for the color of the reset button that resides on the canvas.
    private static final Color RESET_GUESSES_BUTTON_COLOR = new Color(0xff9900);
    
    // Fixed distance from very top of canvas where the meter and chart will
    // be positioned.
    private static final double OFFSET_FROM_TOP = 8;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ModelViewTransform2D _mvt;
    private RadioactiveDatingGameModel _model;
    private PNode _backgroundImageLayer;
    private PNode _backgroundImage;
    private PNode _strataLayer;
    private PNode _guessingGameLayer;
    private AgeGuessingNode _ageGuessingNode;
    private NuclearDecayProportionChart _proportionsChart;
    private RadiometricDatingMeterNode _meterNode;
    private ArrayList<StratumNode> _stratumNodes = new ArrayList<StratumNode>();
    private EdgeOfWorldNode _edgeOfWorld;
    private IdentityHashMap<DatableItem, PNode> _mapDatableItemsToNodes = new IdentityHashMap<DatableItem, PNode>();
    private IdentityHashMap<DatableItem, AgeGuessResultNode> _mapDatableItemsToGuessResults = 
    	new IdentityHashMap<DatableItem, AgeGuessResultNode>();
    private AgeGuessingNode.Listener _ageGuessListener;
    private GradientButtonNode _resetGuessesButtonNode;
    private PPath _transformedViewportBounds = new PPath();
    private AgeGuessResultNode.Listener _clearResultListener;
    private AudioResourcePlayer _audioResourcePlayer;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public RadioactiveDatingGameCanvas(RadioactiveDatingGameModel radioactiveDatingGameModel, AudioResourcePlayer audioResourcePlayer) {

    	_model = radioactiveDatingGameModel;
    	_audioResourcePlayer = audioResourcePlayer;

    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
        _mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.55 )),
        		20,
        		true);
        
        // Register with the meter in the model in order to know when the user
        // changes any of the meter settings, since they need to be propagated
        // to the chart.
        _model.getMeter().addListener(new RadiometricDatingMeter.Adapter(){
        	public void datingElementChanged(){
        		_proportionsChart.clear();
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

        // Add the sky to the background.
        SkyNode sky = new SkyNode(INITIAL_INTERMEDIATE_COORD_WIDTH * 4, INITIAL_INTERMEDIATE_COORD_HEIGHT * 1.5);
        sky.setOffset(_mvt.modelToViewXDouble(0), _mvt.modelToViewYDouble(0));
        _backgroundImageLayer.addChild(sky);
        
        // Add a couple of clouds to the background.
        PImage cloud1 = NuclearPhysicsResources.getImageNode("cloud_1.png");
        cloud1.setScale(0.65);
        cloud1.setOffset(-200, 100);
        _backgroundImageLayer.addChild(cloud1);
        PImage cloud2 = NuclearPhysicsResources.getImageNode("cloud_1.png");
        cloud2.setScale(0.4);
        cloud2.setOffset(700, 270);
        _backgroundImageLayer.addChild(cloud2);
        
        // Load the background image.
        BufferedImage bufferedImage = NuclearPhysicsResources.getImage( "dating_tab_background_only_ground.png" );
        _backgroundImage = new PImage( bufferedImage );
        _backgroundImage.scale(1.2);  // Empirically determined scaling factor, tweak as needed.
        _backgroundImage.setOffset(
        		INITIAL_INTERMEDIATE_COORD_WIDTH / 2 - _backgroundImage.getFullBoundsReference().width / 2,
        		_mvt.modelToViewYDouble(-0.25) - _backgroundImage.getFullBoundsReference().height);
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
        	_strataLayer.addChild(datableItemNode);
        }
        	
        // Add the node that represents the edge of the world.
        _edgeOfWorld = new EdgeOfWorldNode(_model, _mvt);
        addWorldChild(_edgeOfWorld);
        
        // Create the chart that will display relative decay proportions.
        _proportionsChart = new NuclearDecayProportionChart(false, true, false, true);
        configureProportionsChart();
        addWorldChild(_proportionsChart);
        
        // Create the radiometric measuring device.
        _meterNode = new RadiometricDatingMeterNode(_model.getMeter(), 
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_METER_WIDTH_FRACTION,
        		INITIAL_INTERMEDIATE_COORD_HEIGHT * PROPORTIONS_METER_AND_CHART_HEIGHT_FRACTION,
        		_mvt,
        		this,
        		true, 
        		_transformedViewportBounds );
        _meterNode.setMeterBodyOffset( 0, OFFSET_FROM_TOP );
        addWorldChild( _meterNode );
        
        // Set the size and position of the chart.  There are some "tweak
        // factors" in here to make things look good.
        // TODO: If I end up leaving this as a world child, I need to change this
        // resizing call to be a part of the constructor for the chart (I think).
        _proportionsChart.componentResized( new Rectangle2D.Double( 0, 0, 
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_CHART_WIDTH_FRACTION,
        		INITIAL_INTERMEDIATE_COORD_HEIGHT * PROPORTIONS_METER_AND_CHART_HEIGHT_FRACTION ) );
        _proportionsChart.setOffset( 
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_METER_WIDTH_FRACTION + 10,
        		OFFSET_FROM_TOP );

        // Add a button to the canvas for resetting the guesses.  The button
        // remains invisible until the user makes their first guess.
        _resetGuessesButtonNode = new GradientButtonNode(NuclearPhysicsStrings.RESET_GUESSES, 30, 
        		RESET_GUESSES_BUTTON_COLOR);
        _resetGuessesButtonNode.setOffset(_proportionsChart.getFullBoundsReference().getMaxX()
        		- _resetGuessesButtonNode.getFullBoundsReference().width,
        		_proportionsChart.getFullBoundsReference().getMaxY() + 10);
        _resetGuessesButtonNode.setVisible(false);
        _resetGuessesButtonNode.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				clearGuessResults();
				_resetGuessesButtonNode.setVisible(false);
			}
        });
        
        addWorldChild(_resetGuessesButtonNode);
        
        // Draw the decay curve on the chart.
        drawDecayCurveOnChart();
        
        // Create the listener that will be registered with the node that
        // allows the user to guess the age of an item.
        _ageGuessListener = new AgeGuessingNode.Listener(){
			public void guessSubmitted(double ageGuess) {
				handleGuessSubmitted(ageGuess);
			}
        };
        
        // Add the node that will act as the bounds for where the probe can
        // be moved.
        addWorldChild(_transformedViewportBounds);
        
        // Create the listener that will listen for requests from the user
        // to clear a result.
        _clearResultListener = new AgeGuessResultNode.Listener(){
			public void userCleared(AgeGuessResultNode ageGuessResultNode) {
				clearGuessResult(ageGuessResultNode);
			}
        };
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
    		
    		// Set the bounding node to match exactly the size of the
    		// viewport.  This will be used to constrain the movements of the
    		// probe and to position the guessing game windows.
    		AffineTransform transform = getWorldTransformStrategy().getTransform();
    		AffineTransform inverseTransform;
    		try {
    			inverseTransform = transform.createInverse();
    		} catch (NoninvertibleTransformException e) {
    			System.err.println(getClass().getName() + " - Error: Unable to invert transform.");
    			e.printStackTrace();
    			inverseTransform = new AffineTransform(); // Unity transform by default.
    		}
    		Shape tranformedBounds = inverseTransform.createTransformedShape(getBounds());
    		
    		_transformedViewportBounds.setPathTo(tranformedBounds);
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
    		double percentageDecayed = Math.exp(-time*lambda) * 100;
    		_proportionsChart.addDataPoint(time, percentageDecayed);
    	}
    	_proportionsChart.updateMarkerText();
    }
    
    /**
     * Handle a request to clear the results of a previous guess.
     */
    private void clearGuessResult(AgeGuessResultNode ageGuessResultNode){
    	
    	// Remove the node from the canvas.
    	if (_guessingGameLayer.isAncestorOf(ageGuessResultNode)){
    		_guessingGameLayer.removeChild(ageGuessResultNode);
    	}
    	else{
    		System.err.println(getClass().getName() + " - Error: Guessing node not found.");
    		return;
    	}
    	
    	// Remove the mapping to the datable item.
    	DatableItem correspondingDatableItem = null;
        for( DatableItem datableItem : _mapDatableItemsToGuessResults.keySet() ) {
            if(_mapDatableItemsToGuessResults.get(datableItem).equals(ageGuessResultNode)) {
                correspondingDatableItem = datableItem;
                break;
            }
        }

    	if (correspondingDatableItem != null){
    		_mapDatableItemsToGuessResults.remove(correspondingDatableItem);
    	}
    	
    	// If there are no more guess results, hide the button that allows the
    	// user to clear them all.
    	if (_mapDatableItemsToGuessResults.isEmpty()){
    		_resetGuessesButtonNode.setVisible(false);
    	}
    	
    	// If the probe is still touching this datable item, put the guessing
    	// box back up.
    	DatableItem itemBeingTouched = _model.getMeter().getItemBeingTouched();
    	if ((itemBeingTouched == correspondingDatableItem) && (_ageGuessingNode == null)){
    		displayAgeGuessNode(itemBeingTouched);
    	}
    }
    
    /**
     * Handle a notification from the meter that indicates that it has started
     * or stopped touching a datable item.
     */
    private void handleMeterTouchStateChanged(){
    	
    	// Clean up any existing guess submission node.
    	if (_ageGuessingNode != null){
    		_guessingGameLayer.removeChild(_ageGuessingNode);
    		_ageGuessingNode.removeListener(_ageGuessListener);
    		_ageGuessingNode = null;
    	}
    	
    	DatableItem itemBeingTouched = _model.getMeter().getItemBeingTouched();
    	
    	if (itemBeingTouched != null && itemBeingTouched != _model.getDatableAir()){
    		
	    	AgeGuessResultNode previousGuessResultNode = _mapDatableItemsToGuessResults.get(itemBeingTouched);
	    	if (previousGuessResultNode != null){
	    		// The user has previously submitted a guess for this item.
	    		if (previousGuessResultNode.isGuessGood()){
	    			// The previous guess was good.  In this case, we don't need
	    			// to put the dialog up for another guess, so we just bail.
	    			return;
	    		}
	    		else{
	    			// Hide this node so that it doesn't visually interfere
	    			// with the guess entry node.
	    			previousGuessResultNode.setVisible(false);
	    		}
	    	}
	    	
	    	displayAgeGuessNode(itemBeingTouched);
    	}
    	else {
    		// Make sure all previously hidden guess results are now visible,
    		// since one may have been hidden by the guess entry dialog node.
    		for ( AgeGuessResultNode guessResultNode : _mapDatableItemsToGuessResults.values() ) {
    		    guessResultNode.setVisible(true);
    		}
    	}
    }
    
    /**
     * Create and show the node that will allow the user to guess an object's
     * age.
     */
    private void displayAgeGuessNode(DatableItem itemBeingTouched){
		// Create a new guessing box.
		_ageGuessingNode = new AgeGuessingNode(itemBeingTouched );
		
		// Position the guessing box to the side of the node that
		// represents the item being dated.
		PNode datableItemNode = _mapDatableItemsToNodes.get(itemBeingTouched);
		Point2D ageGuessingNodeLocation = new Point2D.Double(0, 0);
		if (datableItemNode == null){
			System.err.println(getClass().getName() + " - Error: Could not locate node for datable item " + itemBeingTouched);
			assert false;
			return;
		}
		else{
			// Position the guessing box.
			ageGuessingNodeLocation = findSpotForWindow(datableItemNode.getFullBounds(), 
					_ageGuessingNode.getFullBounds());
		}
			
		_ageGuessingNode.setOffset(ageGuessingNodeLocation);
		_guessingGameLayer.addChild(_ageGuessingNode);
		
		// Request that the node have focus so that the user doesn't have
		// to click in the edit box on the node before they start entering
		// their guess.
		_ageGuessingNode.requestFocus();
		
		// Register with the node to be informed if and when the user
		// submits a guess.
		_ageGuessingNode.addListener(new AgeGuessingNode.Listener(){
			public void guessSubmitted(double ageGuess) {
				handleGuessSubmitted(ageGuess);
			}
		});
    }
    
    /**
     * Handle the event where the user has submitted a guess of the currently
     * touched item.  The guess is evaluated and a node is presented that
     * indicates whether the user's guess is close enough to be considered
     * correct.
     * 
     * @param ageGuess - Guess of the age in years (note - not in seconds or
     * milliseconds).
     */
    private void handleGuessSubmitted(double ageGuess){

    	DatableItem itemBeingTouched = _model.getMeter().getItemBeingTouched();
    	
    	if (itemBeingTouched == null){
    		System.err.println(getClass().getName() + " - Error: Guess submitted when meter not it contact with datable item.");
    		assert false;
    		return;
    	}
    	
    	// Remove the dialog node where the user submitted the guess.
    	_ageGuessingNode.removeListener(_ageGuessListener);
    	_guessingGameLayer.removeChild(_ageGuessingNode);
    	_ageGuessingNode = null;
    	
    	// Check for a valid guess.
    	if (!((ageGuess >= 0 ) && (ageGuess < Double.POSITIVE_INFINITY))){
    		
    		// The user didn't submit a valid guess, so bail out here, which
    		// effectively ignores the submission.  But first check to see
    		// if a previous guess should be restored.
    		AgeGuessResultNode guessResultNode = _mapDatableItemsToGuessResults.get(itemBeingTouched);
    		if (guessResultNode != null){
    			guessResultNode.setVisible(true);
    		}
    		
    		// And now bail.
    		return;
    	}
    	
    	// Remove any previous guess result nodes for this item.
    	AgeGuessResultNode previousGuessResultNode = 
    		_mapDatableItemsToGuessResults.remove(itemBeingTouched);
    	if (previousGuessResultNode != null){
    		_guessingGameLayer.removeChild(previousGuessResultNode);
    	}
    	
    	// Add a node that indicates to the user whether or not they guessed
    	// correctly.
    	AgeGuessResultNode guessResultNode = new AgeGuessResultNode(ageGuess,
    		determineIfGuessIsGood(HalfLifeInfo.convertYearsToMs(ageGuess), itemBeingTouched));
		_mapDatableItemsToGuessResults.put(itemBeingTouched, guessResultNode);
		guessResultNode.addListener(_clearResultListener);
			
		// Play a little sound to indicate whether the guess is good.
		if (determineIfGuessIsGood(HalfLifeInfo.convertYearsToMs(ageGuess), itemBeingTouched)){
   			_audioResourcePlayer.playSimAudio( "ding.wav" );
		}
		else{
			_audioResourcePlayer.playSimAudio("32_83.wav");
		}
		
		// Position the result indicator.
		PNode datableItemNode = _mapDatableItemsToNodes.get(itemBeingTouched);
		guessResultNode.setOffset( findSpotForWindow(datableItemNode.getFullBounds(),
				guessResultNode.getFullBounds() ) );
		_guessingGameLayer.addChild( guessResultNode );
		
		// Double check that there is at least one valid guess result and,
		// assuming there is, enable the button for clearing it (or them).
		if (_mapDatableItemsToGuessResults.size() > 0){
			_resetGuessesButtonNode.setVisible(true);
		}
		
		// See if there are guesses for every node and whether they are all
		// correct.  If so, show a special dialog indicating that they got
		// everything correct.
		boolean allItemsGuessedAndGuessesCorrect = true;
        for (DatableItem item : _model.getItemIterable()){
        	if (_mapDatableItemsToGuessResults.containsKey(item)){
        		if (!_mapDatableItemsToGuessResults.get(item).isGuessGood()){
        			allItemsGuessedAndGuessesCorrect = false;
        			break;
        		}
        	}
        	else{
        		allItemsGuessedAndGuessesCorrect = false;
        		break;
        	}
        }
        if (allItemsGuessedAndGuessesCorrect){
        	
   			_audioResourcePlayer.playSimAudio( "short-fanfare.wav" );

        	// Put up the dialog.
        	
        	PhetOptionPane.showMessageDialog(this, NuclearPhysicsStrings.GUESSES_CORRECT_MESSAGE,
        			NuclearPhysicsStrings.GUESSES_CORRECT_TITLE);
        }
    }
    
    /**
     * Clear all the currently displayed guess results from the canvas.
     */
    private void clearGuessResults(){
    	Iterator<AgeGuessResultNode> itr = _mapDatableItemsToGuessResults.values().iterator();
    	while (itr.hasNext()){
    		AgeGuessResultNode resultNode = itr.next();
    		_guessingGameLayer.removeChild(resultNode);
    		itr.remove();
    	}
    }
    
    /**
     * Return true if the guessed age is within the tolerance range and false
     * if not.
     * 
     * @param guessedAge - Age in milliseconds.
     * @param datableItem
     * @return
     */
    private boolean determineIfGuessIsGood( double guessedAge, DatableItem datableItem ){

    	double actualAge = datableItem.getRadiometricAge();
    	
    	return ( guessedAge <= actualAge * ( 1 + AGE_GUESS_TOLERANCE_PERCENTAGE / 100 ) ) &&
    		   ( guessedAge >= actualAge * ( 1 - AGE_GUESS_TOLERANCE_PERCENTAGE / 100 ) );
    }

    private void configureProportionsChart(){
    	
        double halfLife = _model.getMeter().getHalfLifeForDating();
        _proportionsChart.setTimeParameters(halfLife * 3.2, halfLife);
        _proportionsChart.setDisplayInfoForNucleusType(_model.getMeter().getNucleusTypeUsedForDating());
        if (_model.getMeter().getNucleusTypeUsedForDating() == NucleusType.CARBON_14){
        	_proportionsChart.setShowCarbonOptions(true);
        }
        else{
        	_proportionsChart.setShowCarbonOptions(false);
        }
    }
    
    /**
     * Find a location on the canvas for a window of the specified size near
     * the specified object.  This is necessary to prevent windows from going
     * off the sides or the top/bottom of the canvas.
     * 
     * @param associatedObjectRect
     * @param windowSize
     * @return
     */
    private Point2D findSpotForWindow(Rectangle2D associatedObjectRect, Rectangle2D windowSize) {
    	
    	double xPos, yPos, maxX, maxY;
    	
    	// Try positioning the node to the right of the associated object.
		xPos = associatedObjectRect.getMaxX() + 8;
		maxX = xPos + windowSize.getWidth();
		yPos = associatedObjectRect.getCenterY() - windowSize.getHeight() / 2;
		maxY = yPos + windowSize.getHeight();
		
		if ( ( maxX > _transformedViewportBounds.getX() + _transformedViewportBounds.getWidth() ) ||
			 ( maxY > _transformedViewportBounds.getY() + _transformedViewportBounds.getHeight() ) ) {
			
			// Some portion of the window will be off the canvas if we use the
			// default position, so we need to try alternatives.
			
			if ( maxX > _transformedViewportBounds.getX() + _transformedViewportBounds.getWidth() ){
				// The window would be off the right edge of the canvas, so
				// set its x position such that it just fits in the x
				// direction.
				xPos = _transformedViewportBounds.getX() + _transformedViewportBounds.getWidth() - windowSize.getWidth() - 8;
				
				// Set the Y position so that it is just below the object.
				// This will be checked and possibly changed below.
				yPos = associatedObjectRect.getMaxY();
				maxY = yPos + windowSize.getHeight();
			}
			
			if ( maxY > _transformedViewportBounds.getY() + _transformedViewportBounds.getHeight() ){
				// The window would be off the bottom of the canvas if left
				// unadjusted.
				if (associatedObjectRect.getMaxX() + windowSize.getWidth() < 
					_transformedViewportBounds.getX() + _transformedViewportBounds.getWidth()){
					// The window can fit between the right edge of the object
					// and the right edge of the canvas, so position it at the
					// bottom of the canvas.
					yPos = _transformedViewportBounds.getY() + _transformedViewportBounds.getHeight() 
							- windowSize.getHeight() - 8;
				}
				else{
					// Position it above the item.
					yPos = associatedObjectRect.getY() - windowSize.getHeight();
				}
			}
		}

    	return new Point2D.Double(xPos, yPos);
    }
}