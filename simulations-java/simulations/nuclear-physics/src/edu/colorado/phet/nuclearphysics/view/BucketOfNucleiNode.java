/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections.functors.PrototypeFactory;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.glaciers.view.tools.AbstractToolNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayModel.Listener;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This class implements a user interface component that looks like a bucket
 * filled with nuclei.  The user can grab nuclei from the bucket and drag then
 * onto the canvas.
 * 
 * @author John Blanco
 */
public class BucketOfNucleiNode extends PNode {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	public static final Stroke LINE_STROKE = new BasicStroke( 0.5f );
	public static final Color OUTER_COLOR_DARK = new Color (0xAA7700);
	public static final Color OUTER_COLOR_LIGHT = new Color (0xFF9933);
	public static final Color INNER_COLOR_DARK = new Color (0xAA7700);
	public static final Color INNER_COLOR_LIGHT = new Color (0xCC9933);
	public static final double PROTOTYPICAL_NUCLEUS_WIDTH = 10;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private ArrayList _listeners = new ArrayList();
    private PNode _backOfBucketLayer;
    private PNode _backInteriorLayer;
    private PNode _middleInteriorLayer;
    private PNode _frontInteriorLayer;
    private PNode _frontOfBucketLayer;
    private HTMLNode _bucketLabel;
    private GrabbableLabeledNucleusNode.Listener _grabbableNucleusListener;
    private AtomicNucleusNode [] _visibleNucleusNodes;
    private double _bucketHeight;
    private double _bucketWidth;
    private double _ellipseVerticalSpan;
    int _numVisibleNucleiInMiddleLayer;
    int _numVisibleNucleiInOuterLayers;
    PNode _nodeBeingDragged;
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
	/**
	 * Constructor - This takes a width and height in world coordinates and
	 * creates a bucket of corresponding size.
	 */
	public BucketOfNucleiNode(double width, double height){
		
		// Local allocation and initialization.
		_bucketHeight = height;
		_bucketWidth = width;
		_ellipseVerticalSpan = height * 0.4; // Arbitrary sizing, can be changed to alter appearance.
		
		// Create the gradient paints that will be used to paint the bucket.
		GradientPaint outerPaint = new GradientPaint(0, (float)height/2, OUTER_COLOR_DARK, 
        		(float)width, (float)height/2, OUTER_COLOR_LIGHT);
		
		GradientPaint innerPaint = new GradientPaint(0, (float)height/2, INNER_COLOR_LIGHT, 
        		(float)width, (float)height/2, INNER_COLOR_DARK);
		
		/*
		// TODO: JPB TBD - A basic rect for guidance, remove when this node is done.
		PhetPPath outerRect = new PhetPPath(new Rectangle2D.Double( 0, 0, width, height ));
		outerRect.setStroke( new BasicStroke( 0.25f ) );
		outerRect.setStrokePaint(Color.red);
		addChild(outerRect);
		*/
		
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
		GeneralPath bucketBodyPath = new GeneralPath();
		bucketBodyPath.moveTo(0, (float)(_ellipseVerticalSpan / 2));
		bucketBodyPath.lineTo((float)(width * 0.1), (float)(height * 0.8));
		bucketBodyPath.quadTo((float)(width / 2), (float)(height * 1.1), (float)(width * 0.9), (float)(height * 0.8));
		bucketBodyPath.lineTo((float)(width), (float)(_ellipseVerticalSpan / 2));
		bucketBodyPath.quadTo((float)(width / 2), (float)(height * 0.6), 0, (float)(_ellipseVerticalSpan / 2));
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
		
		// Draw the handle.
		PhetPPath bucketHandle = new PhetPPath( new QuadCurve2D.Double(width/2, _ellipseVerticalSpan, width * 1.6, 
				_ellipseVerticalSpan * 1.3, width, _ellipseVerticalSpan / 2) );
		bucketHandle.setStroke( LINE_STROKE );
		_frontOfBucketLayer.addChild(bucketHandle);
		
		// Set up some values that will be used when filling the bucket with
		// nuclei.
		_numVisibleNucleiInMiddleLayer = (int)(_bucketWidth / PROTOTYPICAL_NUCLEUS_WIDTH);
		_numVisibleNucleiInOuterLayers = (int)((double)_numVisibleNucleiInMiddleLayer * 0.75);
		_visibleNucleusNodes = new AtomicNucleusNode[((2 * _numVisibleNucleiInOuterLayers) + _numVisibleNucleiInMiddleLayer)];
	}
	
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
	
	/**
	 * Add a nucleus node to the bucket.  Note that the nucleus will be moved
	 * to an appropriate location and the node may be hidden if the bucket is
	 * already looking like it is full.
	 */
	public void addNucleus( AtomicNucleusNode nucleusNode ){
		
		// See if there are any slots open in the list of visible nuclei.
		int i;
		boolean nucleasNodeIsVisible = false;
		for (i = 0; i < _visibleNucleusNodes.length; i++){
			if (_visibleNucleusNodes[i] == null){
				// This spot is available, so add this nucleus.
				nucleusNode.setVisible(true);
				_visibleNucleusNodes[i] = nucleusNode;
				addAndPositionVisibleNucleus(i);
				nucleasNodeIsVisible = true;
				break;
			}
		}
		
		if (!nucleasNodeIsVisible){
			// Move the node to be in the center of the bucket.
			nucleusNode.getNucleusRef().setPosition(_bucketWidth / 2, _bucketHeight / 2);
			
			// Invisible nuclei are kept as children of the main node.
			addChild(nucleusNode);
			
			// And finally, make sure the node is invisible.
			nucleusNode.setVisible(false);
		}
	}
	
	/**
	 * Remove a nucleus node from the bucket.
	 */
	public void removeNucleus( AtomicNucleusNode nucleusNode ){
		
		// See if it is in the list of visible nuclei and remove it if so.
		boolean nucleusWasVisible = false;
		for (int i = 0; i < _visibleNucleusNodes.length; i++){
			if (_visibleNucleusNodes[i] == nucleusNode){
				nucleusWasVisible = true;
				_visibleNucleusNodes[i] = null;
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
			// Remove this node as a child of the main node.
			if (removeChild( nucleusNode ) == null){
				System.err.println("ERROR: Requested node is not in the bucket.");
			}
		}
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

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void notifyNucleusExtracted(PNode nucleusNode){
        for (int i = 0; i < _listeners.size(); i++){
            ((Listener)_listeners.get( i )).nucleusExtracted(nucleusNode);
        }        
    }

	/**
	 * Position one of the visible nuclei based on its position with the array
	 * of all visible nuclei and add it as a child of the appropriate node.
	 * 
	 * @param nucleusIndex
	 */
	private void addAndPositionVisibleNucleus(int nucleusIndex){

		double xPos, yPos;
		
		if (nucleusIndex < _numVisibleNucleiInOuterLayers){
			// This nucleus is in the back row.
			_backInteriorLayer.addChild(_visibleNucleusNodes[nucleusIndex]);
			xPos = PROTOTYPICAL_NUCLEUS_WIDTH + PROTOTYPICAL_NUCLEUS_WIDTH * nucleusIndex * 1.1;
			yPos = _ellipseVerticalSpan * 0.25;
		}
		else if (nucleusIndex < _numVisibleNucleiInMiddleLayer + _numVisibleNucleiInOuterLayers){
			// This nucleus is in the middle row.
			_middleInteriorLayer.addChild(_visibleNucleusNodes[nucleusIndex]);
			xPos = PROTOTYPICAL_NUCLEUS_WIDTH * 0.6 + 
			    PROTOTYPICAL_NUCLEUS_WIDTH * (nucleusIndex - _numVisibleNucleiInOuterLayers);
			yPos = _ellipseVerticalSpan * 0.65;
		}
		else{
			// This nucleus is in the front row.
			_frontInteriorLayer.addChild(_visibleNucleusNodes[nucleusIndex]);
			xPos = PROTOTYPICAL_NUCLEUS_WIDTH + 
			    PROTOTYPICAL_NUCLEUS_WIDTH * (nucleusIndex - _numVisibleNucleiInOuterLayers - _numVisibleNucleiInMiddleLayer) * 1.1;
			yPos = _ellipseVerticalSpan * 1.1;
		}
		
		// Position the nucleus within the model, which will then be sent as
		// a position change event to the node.
		_visibleNucleusNodes[nucleusIndex].getNucleusRef().setPosition(xPos, yPos);
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
					if (childNode instanceof AtomicNucleusNode){
						// Add this to the list of visible nuclei.
						childNode.setVisible(true);
						_visibleNucleusNodes[i] = (AtomicNucleusNode)childNode;
						addAndPositionVisibleNucleus(i);
						break;
					}
				}
			}
		}
	}

	private void updateLabelText(){
		
		// TODO: JPB TBD - Not at all done yet.  Needs to use string resources, handle different atoms.
		_bucketLabel.setHTML("<html>Bucket o' <br>Polonium</html>");
		double desiredWidth = _bucketWidth * 0.5;
		_bucketLabel.setScale(desiredWidth / _bucketLabel.getFullBoundsReference().width);
		_bucketLabel.setOffset((_bucketWidth / 2) - (_bucketLabel.getFullBounds().width / 2), _bucketHeight * 0.4);
	}
	
    //------------------------------------------------------------------------
    // Inner interfaces
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
}
