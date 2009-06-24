/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicSliderUI;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.common.view.AbstractAtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusAlphaDecayCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class implements a user interface component that looks like a bucket
 * filled with nuclei.  The user can grab nuclei from the bucket and drag then
 * onto the canvas.
 * 
 * NOTE: As of this writing (April 2009), there is an assumption that all the
 * nuclei in the bucket are the same.  Nuclei of different sizes are not
 * handled well, and this would need to be modified if that functionality is
 * needed.
 * 
 * @author John Blanco
 */
public class BucketOfNucleiNode extends PNode {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Stroke LINE_STROKE = new BasicStroke( 0.5f );
	private static final Color DEFAULT_COLOR = new Color (0xffaa00);
	private static final double DEFAULT_ANGLE = Math.PI/6;
	private static final double LOWER_TO_UPPER_ELLIPSE_HEIGHT_RATIO = 0.85;
	
	// Controls whether a rectangle that shows the size and position of the
	// entire bucket is visible.  Primarily for debugging.
	private static final boolean SHOW_BUCKET_RECT = false;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private ArrayList _listeners = new ArrayList();
    private PPath _bucketRect;
    private PNode _backOfBucketLayer;
    private PNode _backInteriorLayer;
    private PNode _middleInteriorLayer;
    private PNode _frontInteriorLayer;
    private PNode _frontOfBucketLayer;
    private HTMLNode _bucketLabel;
    private PImage _radiationSymbolNode;
    private AbstractAtomicNucleusNode [] _visibleNucleusNodes;
    private double _bucketHeight;
    private double _bucketWidth;
    private double _ellipseVerticalSpan;
    int _numVisibleNucleiInMiddleLayer;
    int _numVisibleNucleiInOuterLayers;
    PNode _nodeBeingDragged;
    ArrayList _shrinkAnimationTimers;
	private NucleusType _nucleusType;
	private double _nucleusWidth = 0;
	private boolean _showLabel = true;             // Label is shown by default.
	private boolean _showRadiationSymbol = true;   // Icon is shown by default.
	private Color _baseColor;
	private PSwing _sliderNode = null;
	private NormalizedSlider _slider;
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
	/**
	 * Constructor - This takes a width and height in world coordinates as
	 * well as a base color value and creates a bucket of corresponding size.
	 * 
	 * @param width - desired width of the bucket
	 * @param height - desired height of the bucket
	 * @param tiltAngle - angle of tilt in radians - ONLY WORKS OVER A VERY LIMITED RANGE
	 * @param baseColor - base color from which gradients are created
	 */
	public BucketOfNucleiNode( double width, double height, double tiltAngle, Color baseColor ){
		
		// Local allocation and initialization.
		_bucketHeight = height;
		_bucketWidth = width;
		_shrinkAnimationTimers = new ArrayList();
		_nucleusType = NucleusType.POLONIUM_211;
		_baseColor = baseColor;
		
		// Create and add the background rectangle.
		_bucketRect = new PPath(new Rectangle2D.Double(0, 0, width, height));
		_bucketRect.setPaint(Color.BLUE);
		_bucketRect.setVisible(SHOW_BUCKET_RECT);
		addChild(_bucketRect);
		
		// Create the illusion of tilt based on the supplied angle.
		_ellipseVerticalSpan = height * tiltAngle * (2 / Math.PI);
		
		// Create the gradient paints that will be used to paint the bucket.
		Color outerColorLight = baseColor;
		Color outerColorDark = ColorUtils.darkerColor( baseColor, 0.5 );
		Color innerColorDark = ColorUtils.darkerColor( outerColorDark, 0.5 );
		Color innerColorLight = outerColorDark;
		GradientPaint outerPaint = new GradientPaint(0, (float)height/2, outerColorDark, 
        		(float)width, (float)height/2, outerColorLight);
		
		GradientPaint innerPaint = new GradientPaint(0, (float)height/2, innerColorLight, 
        		(float)width, (float)height/2, innerColorDark);
		
		// Create a layering effect using PNodes so that we can create the
		// illusion of three dimensions.
		_backOfBucketLayer = new PNode();
		addChild( _backOfBucketLayer );
		_backInteriorLayer = new PNode();
		addChild( _backInteriorLayer );
		_middleInteriorLayer = new PNode();
		addChild( _middleInteriorLayer );
		_frontInteriorLayer = new PNode();
		addChild( _frontInteriorLayer );
		_frontOfBucketLayer = new PNode();
		addChild( _frontOfBucketLayer );
		
		// Draw the inside of the bucket.
		PhetPPath ellipseInBackOfBucket = new PhetPPath(new Ellipse2D.Double( 0, 0, width, _ellipseVerticalSpan ));
		ellipseInBackOfBucket.setStroke( LINE_STROKE );
		ellipseInBackOfBucket.setPaint(innerPaint);
		_backOfBucketLayer.addChild(ellipseInBackOfBucket);

		// Draw the outside of the bucket.
		double bottomEllipseVerticalSpan = _ellipseVerticalSpan * LOWER_TO_UPPER_ELLIPSE_HEIGHT_RATIO;
		GeneralPath bucketBodyPath = new GeneralPath();
		bucketBodyPath.moveTo(0, (float)(_ellipseVerticalSpan / 2));
		bucketBodyPath.lineTo((float)(width * 0.1), (float)(height - bottomEllipseVerticalSpan / 2));
		bucketBodyPath.quadTo((float)(width / 2), (float)(height + _ellipseVerticalSpan * 0.4), (float)(width * 0.9), (float)(height - bottomEllipseVerticalSpan / 2));
		bucketBodyPath.lineTo((float)(width), (float)(_ellipseVerticalSpan / 2));
		bucketBodyPath.quadTo((float)(width / 2), (float)(_ellipseVerticalSpan * 1.5), 0, (float)(_ellipseVerticalSpan / 2));
		bucketBodyPath.closePath();
		PhetPPath bucketBody = new PhetPPath( bucketBodyPath );
		bucketBody.setStroke( LINE_STROKE );
		bucketBody.setPaint(outerPaint);
		_frontOfBucketLayer.addChild(bucketBody);
		
		// Add the label to the bucket.
		_bucketLabel = new HTMLNode();
		_bucketLabel.setFont( new PhetFont(12));
		_frontOfBucketLayer.addChild(_bucketLabel);
		updateLabelText();
		
		// Add the radiation symbol to the bucket.
        // Load the graphic image for this device.
        _radiationSymbolNode = NuclearPhysicsResources.getImageNode("RadiationSymbolWithPerspective.png");
        _radiationSymbolNode.scale( 0.28 * (_bucketHeight / _radiationSymbolNode.getWidth()));
        _radiationSymbolNode.setOffset(_bucketWidth * 0.75, _bucketHeight * 0.4);
        _frontOfBucketLayer.addChild(_radiationSymbolNode);
		
		// Draw the handle.
		PhetPPath bucketHandle = new PhetPPath( new QuadCurve2D.Double(width/2, _ellipseVerticalSpan, width * 1.6, 
				_ellipseVerticalSpan * 1.3, width, _ellipseVerticalSpan / 2) );
		bucketHandle.setStroke( LINE_STROKE );
		_frontOfBucketLayer.addChild(bucketHandle);
	}

	/**
	 * Constructor that uses default tilt angle and color.
	 * 
	 * @param width
	 * @param height
	 */
	public BucketOfNucleiNode( double width, double height ){
		this( width, height, DEFAULT_ANGLE, DEFAULT_COLOR );
	}

	/**
	 * Constructor that uses default tilt angle.
	 * 
	 * @param width
	 * @param height
	 */
	public BucketOfNucleiNode( double width, double height, Color color ){
		this( width, height, DEFAULT_ANGLE, color );
	}

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
	
	/**
	 * Add a nucleus node to the bucket.  Note that the nucleus will be moved
	 * to an appropriate location and the node may be hidden if the bucket is
	 * already looking like it is full.
	 */
	public void addNucleus( AbstractAtomicNucleusNode nucleusNode ){
		
		if ( isEmpty() ){
			// This is the first nucleus being added, so now we know the
			// width of nuclei that will be kept here and we can calculate how
			// many will fit.
			_nucleusWidth = nucleusNode.getFullBoundsReference().getWidth();
			_numVisibleNucleiInMiddleLayer = (int)(_bucketWidth / _nucleusWidth);
			_numVisibleNucleiInOuterLayers = (int)((double)_numVisibleNucleiInMiddleLayer * 0.75);
			_visibleNucleusNodes = new AbstractAtomicNucleusNode[((2 * _numVisibleNucleiInOuterLayers) + 
					_numVisibleNucleiInMiddleLayer)];
		}
		
		// See if there are any slots open in the list of visible nuclei.
		boolean openSpotFound = false;
		for (int i = 0; i < _visibleNucleusNodes.length; i++){
			if (_visibleNucleusNodes[i] == null){
				// This spot is available, so add this nucleus.
				nucleusNode.setVisible(true);
				_visibleNucleusNodes[i] = nucleusNode;
				positionVisibleNucleus(i);
				openSpotFound = true;
				break;
			}
		}
		
		if (!openSpotFound){
			// No open spot found amongst the the visible nuclei, so make this
			// one invisible.
			nucleusNode.setVisible(false);
			
			// Invisible nuclei are kept as children of the main node.
			addChild(nucleusNode);
		}
	}
	
	/**
	 * Add a nucleus to this node in such a way that it appears to shrink.
	 * 
	 * @param nucleusNode - Nucleus node to be added to the bucket.
	 */
	public void addNucleusAnimated(AbstractAtomicNucleusNode nucleusNode){
		
		// Create a timer that will step through the animation and, when done,
		// will add the node.
		_shrinkAnimationTimers.add(new ShrinkAnimationTimer(nucleusNode));
	}
	
	/**
	 * Remove a specific nucleus node from the bucket.
	 */
	public void removeNucleus( AbstractAtomicNucleusNode nucleusNode ){
		
		// See if it is in the list of visible nuclei and remove it if so.
		boolean nucleusWasVisible = false;
		for (int i = 0; i < _visibleNucleusNodes.length; i++){
			if (_visibleNucleusNodes[i] == nucleusNode){
				nucleusWasVisible = true;
				_visibleNucleusNodes[i] = null;
				// TODO: JPB TBD - I think it is not actually necessary to remove
				// this node as a child of the interior nodes, since I believe
				// Piccolo does this automatically.  So at some point, test and see
				// if the following code can be removed.
				if (_backInteriorLayer.isAncestorOf(nucleusNode) ){
					_backInteriorLayer.removeChild(nucleusNode);
				}
				else if (_middleInteriorLayer.isAncestorOf(nucleusNode)){
					_middleInteriorLayer.removeChild(nucleusNode);
				}
				else if (_frontInteriorLayer.isAncestorOf(nucleusNode)){
					_frontInteriorLayer.removeChild(nucleusNode);
				}
				break;
			}
		}
		
		if (nucleusWasVisible){
			fillEmptyVisibleSlots();
		}
		else{
			// Nucleus was not visible.  See if it is one of the invisible children.
			if (isAncestorOf(nucleusNode)){
				removeChild(nucleusNode);
			}
			else{
				// See if it is currently being animated.
				boolean nodeFound = false;
				for (int i = 0; i < _shrinkAnimationTimers.size(); i++){
					ShrinkAnimationTimer shrinkTimer = (ShrinkAnimationTimer)_shrinkAnimationTimers.get(i);
					if (shrinkTimer.getShrinkingNode() == nucleusNode){
						// This is the nucleus node that we are looking for.
						// Stop the timer and remove it from the list.
						shrinkTimer.stop();
						shrinkTimer.clearShrinkingNode();
						_shrinkAnimationTimers.remove(shrinkTimer);
						nodeFound = true;
						break;
					}
				}
				if (!nodeFound){
					// The requested node was never found, which may mean
					// that the caller is trying to remove a node that simply
					// isn't in the bucket, but it could also indicate some
					// problem exists in this object's implementation.
					System.err.println("Warning: Unable to locate and remove node.");
				}
			}
		}
	}
	
	/**
	 * Pick an available nucleus (any one will do) and return it to the caller.
	 * 
	 * @return - Reference to the nucleus, null if the bucket is empty.
	 */
	public AbstractAtomicNucleusNode extractAnyNucleusFromBucket(){
		
		// Pull a nucleus off the list of visible nuclei.  By design, if this
		// list is empty then the bucket is empty.
		
		AbstractAtomicNucleusNode extractedNode = null;
		
		for (int i = 0; i < _visibleNucleusNodes.length; i++){
			if (_visibleNucleusNodes[i] != null){
				extractedNode = _visibleNucleusNodes[i];
				_visibleNucleusNodes[i] = null;
				break;
			}
		}
		
		if (extractedNode != null){
			// Fill the vacated visible slot.
			fillEmptyVisibleSlots();
		}
		
		return extractedNode;
	}
	
	/**
	 * Returns a boolean value indicating whether the specified node is "in
	 * the bucket", meaning that it is a child (in the PNode sense) of this
	 * node.
	 */
	public boolean isNodeInBucket( PNode node ){
		
		return isAncestorOf(node);
	}
	
    /**
     * This method allows the caller to register for notifications from this
     * object.
     * 
     * @param listener
     */
    public void addListener(Listener listener)
    {
        if ( !_listeners.contains( listener ) ){
            _listeners.add( listener );
        }
    }
    
    public void setNucleusType( NucleusType nucleusType ){
    	_nucleusType  = nucleusType;
    	updateLabelText();
    }
    
    public void setShowLabel( boolean enabled ){
    	_showLabel = enabled;
    	updateLabelText();
    }
    
    public void setShowRadiationSymbol( boolean enabled ){
    	_showRadiationSymbol = enabled;
    	_radiationSymbolNode.setVisible(_showRadiationSymbol);
    }
    
    public void setSliderEnabled( boolean enabled ){
    	
    	// If the slider doesn't exist yet, create it.
    	if (_sliderNode == null && enabled){
        	_slider = new NormalizedSlider();
        	_slider.setPreferredSize(new Dimension((int)(_bucketWidth * 0.8),(int)(_bucketHeight * 0.5)));
        	_slider.setBackground(ColorUtils.darkerColor(_baseColor,0.5));
        	_slider.setForeground(Color.GREEN);
            _slider.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
        	
        	// Wrap the slider in a PSwing so that it can be used in the play area.
        	PSwing sliderNode = new PSwing(_slider);
        	sliderNode.setOffset(_bucketWidth / 2 - sliderNode.getFullBounds().width / 2,
        			_bucketHeight / 2 - sliderNode.getFullBounds().height / 2);
        	
        	_frontOfBucketLayer.addChild(sliderNode);
    	}
    	else{
    		_sliderNode.setVisible(enabled);
    	}
    }
    
    /**
     * Get the slider control that may be part of the bucket.  Note that this
     * may return null if the slider has never been enabled.
     */
    public NormalizedSlider getSlider(){
    	return _slider;
    }
    
    public void resetSliderPosition(){
    	if (_slider != null){
    		_slider.setValue(0);
    	}
    }

    /**
     * Set the offset of this node.  This has been overridden so that only the
     * visible portions of the bucket will be moved, but the PNodes where the
     * particles are maintained are NOT offset.  This allows the nuclei to be
     * maintained in the model locations that correspond to the location of
     * the bucket, so that the model coordinates don't have to be adjusted
     * when nuclei are added/removed from the bucket.
     */
	@Override
	public void setOffset(double x, double y) {
		_bucketRect.setOffset(x, y);
	    _backOfBucketLayer.setOffset(x, y);
	    _frontOfBucketLayer.setOffset(x, y);
	}

	@Override
	public void setOffset(Point2D point) {
		setOffset(point.getX(), point.getY());
	}

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
	/**
	 * Position one of the visible nuclei based on its position with the array
	 * of all visible nuclei and add it as a child of the appropriate node.
	 * 
	 * @param nucleusIndex
	 */
	private void positionVisibleNucleus(int nucleusIndex){

		double xPos, yPos;
		
		AbstractAtomicNucleusNode nucleus = _visibleNucleusNodes[nucleusIndex];
		
		if (nucleusIndex < _numVisibleNucleiInOuterLayers){
			// This nucleus is in the back row.
			_backInteriorLayer.addChild(nucleus);
			xPos = _nucleusWidth * 1.3 + _nucleusWidth * nucleusIndex * 1.1;
			yPos = _ellipseVerticalSpan * 0.20;
		}
		else if (nucleusIndex < _numVisibleNucleiInMiddleLayer + _numVisibleNucleiInOuterLayers){
			// This nucleus is in the middle row.
			_middleInteriorLayer.addChild(nucleus);
			xPos = _nucleusWidth * 0.8 + _nucleusWidth * (nucleusIndex - _numVisibleNucleiInOuterLayers);
			yPos = _ellipseVerticalSpan * 0.50;
		}
		else{
			// This nucleus is in the front row.
			_frontInteriorLayer.addChild(nucleus);
			xPos = _nucleusWidth * 1.7 + _nucleusWidth *
			    (nucleusIndex - _numVisibleNucleiInOuterLayers - _numVisibleNucleiInMiddleLayer) * 1.1;
			yPos = _ellipseVerticalSpan * 0.80;
		}
		
		// Position the nucleus within the model, which will then be sent as
		// a position change event to the node.
		nucleus.getNucleusRef().setPosition(xPos + _bucketRect.getFullBounds().x, 
				yPos + _bucketRect.getFullBounds().y);
	}
	
	/**
	 * Go through the list of visible nuclei and fill any unoccupied slots
	 * with invisible nuclei and make them visible.
	 */
	private void fillEmptyVisibleSlots(){
		for (int i = 0; i < _visibleNucleusNodes.length; i++){
			if (_visibleNucleusNodes[i] == null){
				// Fill this slot.
				for (int j = 0; j < getChildrenCount(); j++){
					PNode childNode = getChild(j);
					if (childNode instanceof AbstractAtomicNucleusNode){
						// Add this to the list of visible nuclei.
						childNode.setVisible(true);
						_visibleNucleusNodes[i] = (AbstractAtomicNucleusNode)childNode;
						positionVisibleNucleus(i);
						break;
					}
				}
			}
		}
	}
	
	private boolean isEmpty(){

		boolean isEmpty = true;
		
		// See if there are any nodes.  Note that this relies on the idea that
		// if there are any nodes in the bucket, they will be visible.
		if ( _visibleNucleusNodes != null ){
			for ( int i = 0; i < _visibleNucleusNodes.length; i++ ){
				if ( _visibleNucleusNodes[i] != null ){
					// Bucket is not empty.
					isEmpty = false;
					break;
				}
			}
		}
		
		return isEmpty;
	}

	/**
	 * Update the textual label on the front of the bucket.
	 */
	private void updateLabelText(){
		
		// Set the text of the label.
		switch (_nucleusType){
		case POLONIUM_211:
			_bucketLabel.setHTML(NuclearPhysicsStrings.BUCKET_LABEL_POLONIUM);
		    break;
			
		case CUSTOM:
			_bucketLabel.setHTML(NuclearPhysicsStrings.BUCKET_LABEL_ATOMS);
		    break;
		    
		case CARBON_14:
			_bucketLabel.setHTML(NuclearPhysicsStrings.BUCKET_LABEL_ATOMS);
		    break;
		    
		default:
			_bucketLabel.setHTML(NuclearPhysicsStrings.BUCKET_LABEL_ATOMS);
		    break;
		}
		
		// Do a substitution of "of" with "o'" just for the sheer coolness of
		// it.  This was requested by Noah P.  Hopefully this won't mess up
		// any of the translations, but if it turns out that it does it should
		// be removed.
		if (_bucketLabel.getHTML().indexOf("Bucket of") != 0){
			_bucketLabel.setHTML(_bucketLabel.getHTML().replaceFirst("Bucket of", "Bucket o'"));
		}

		// Scale and position the label.
		double desiredWidth = _bucketWidth * 0.5;
		_bucketLabel.setScale(1);
		_bucketLabel.setScale(desiredWidth / _bucketLabel.getFullBoundsReference().width);
		_bucketLabel.setOffset((_bucketWidth / 2) - (_bucketLabel.getFullBounds().width / 2), _bucketHeight * 0.4);
		
		// Set the visibility.
		_bucketLabel.setVisible(_showLabel);
	}

    /**
     * Main routine for stand alone testing.
     * 
     * @param args
     */
    public static void main(String [] args){
    	
        final BucketOfNucleiNode bucket = new BucketOfNucleiNode(300, 200, Math.PI/6, Color.GREEN); 

        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
        frame.setContentPane( canvas );
        canvas.addScreenChild( bucket );
        bucket.setOffset(50, 50);
        frame.setSize( 800, 400 );
        frame.setVisible( true );
    }
	
    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
    
    public static interface Listener {
        /**
         * This informs the listener that an atomic nucleus was pulled out
         * of the bucket and dropped in the play area.
         * 
         * @param _nucleusNode - nucleus that was moved into the play area.
         */
        public void nucleusExtracted(PNode nucleus);
    }
    
    public static class NormalizedSlider extends JSlider{
    	
    	private static final int MAX_SLIDER_VALUE = 1000;
    	
		public NormalizedSlider(){
    		super(JSlider.HORIZONTAL, 0, MAX_SLIDER_VALUE, 0);
    	}
    	
		public double getNormalizedReading(){
			return ((double)getValue() / (double)MAX_SLIDER_VALUE);
		}
    }
    
    // TODO: The class below was part of an experiment to change the size of
    // the slider knob on the bucket.  It kind of worked, but is likely to
    // have issues on the Mac.  I'm going to check with the physicists if
    // having a larger slider is really worth the amount of work that it
    // appears to entail.  If not, this can go.  Jblanco, June 24 2009.
    private class MySliderUI extends BasicSliderUI {

		public MySliderUI(JSlider slider) {
			super(slider);
		}

		@Override
		protected Dimension getThumbSize() {
			Dimension oldDimension = super.getThumbSize();
			return new Dimension(20, 40);
		}

		@Override
		protected void calculateTrackRect() {
			// TODO Auto-generated method stub
			super.calculateTrackRect();
		}
    }

    /**
     * Timer used to animate the shrinking of a nucleus when it is taken in
     * to the bucket.
     */
    private class ShrinkAnimationTimer extends Timer {

    	private static final int TIMER_DELAY = 30;          // Milliseconds between each animation step.
    	
    	private static final double SHRINKAGE_RATE = 0.80;   // Amount of size change per timer firing, smaller number
    	                                                    // means faster shrinking.
    	
    	private static final int INITIAL_SHRINK_COUNT = 20; // Number of shrinking steps before disappearing.
    	
    	private AbstractAtomicNucleusNode _shrinkingNode;
    	private int _shrinkCount;
    	
		public ShrinkAnimationTimer(AbstractAtomicNucleusNode node) {
			super(TIMER_DELAY, null);
			
			_shrinkingNode = node;
			_shrinkCount = INITIAL_SHRINK_COUNT;
			
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _shrinkCount--;
                    if ( _shrinkCount <= 0 ) {
                        stop();                              // Stop the timer.
                        _shrinkingNode.setScale(1);
                        _shrinkingNode.setScale(MultiNucleusAlphaDecayCanvas.SCALING_FACTOR_FOR_NUCLEUS_NODES_IN_BUCKET);
                        addNucleus(_shrinkingNode);          // Add the node to the bucket.
                        _shrinkingNode = null;               // Remove our reference to the node (for cleanup).
                        
                        // Remove ourself from the list of currently running animation timers.
                        _shrinkAnimationTimers.remove(ShrinkAnimationTimer.this);
                    }
                    else{
                    	// Shrink the node.
                        _shrinkingNode.scale( SHRINKAGE_RATE );
                    }
                }
            });
            
            // Start the timer running.
            start();
		}
		
		public AbstractAtomicNucleusNode getShrinkingNode(){
			return _shrinkingNode;
		}
		
		public void clearShrinkingNode(){
			stop();  // Make sure that the time is stopped.
			_shrinkingNode = null;
		}
    }
}
