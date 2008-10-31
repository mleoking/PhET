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

public class BucketOfNucleiNode extends PNode {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	public static final Stroke LINE_STROKE = new BasicStroke( 0.5f );
	public static final Color OUTER_COLOR_DARK = new Color (0xAA7700);
	public static final Color OUTER_COLOR_LIGHT = new Color (0xFF9933);
	public static final Color INNER_COLOR_DARK = new Color (0xAA7700);
	public static final Color INNER_COLOR_LIGHT = new Color (0xCC9933);
	public static final double NUCLEUS_WIDTH_PROPORTION = 0.25;
	
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
    private PNode [] _nucleusNodes;
    private double _bucketHeight;
    private double _bucketWidth;
    private double _ellipseVerticalSpan;
    int _numNucleiInMiddleLayer;
    int _numNucleiInOuterLayers;
    PNode _nodeBeingDragged;
    
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
	/**
	 * Constructor - This takes a width and height in world coordinates.
	 */
	public BucketOfNucleiNode(double width, double height){
		
		// Local allocation and initialization.
		_bucketHeight = height;
		_bucketWidth = width;
		
		// Create the gradient paints that will be used to paint the bucket.
		GradientPaint outerPaint = new GradientPaint(0, (float)height/2, OUTER_COLOR_DARK, 
        		(float)width, (float)height/2, OUTER_COLOR_LIGHT);
		
		GradientPaint innerPaint = new GradientPaint(0, (float)height/2, INNER_COLOR_LIGHT, 
        		(float)width, (float)height/2, INNER_COLOR_DARK);
		
		// TODO: JPB TBD - A basic rect for guidance, remove when this node is done.
		PhetPPath outerRect = new PhetPPath(new Rectangle2D.Double( 0, 0, width, height ));
		outerRect.setStroke( new BasicStroke( 0.25f ) );
		outerRect.setStrokePaint(Color.red);
		addChild(outerRect);
		
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
		
		_ellipseVerticalSpan = height * 0.4;
		
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
		
		// Create the listener object that will be registered with the
		// grabbable nuclei.  We do this in a non-anonymous way so that we can
		// unregister later.
		_grabbableNucleusListener = new GrabbableLabeledNucleusNode.Listener(){
			
			public void nodeGrabbed(GrabbableLabeledNucleusNode node){
				System.out.println("Bucket got the grab notification.");
				_nodeBeingDragged = node;
				for (int i=0; i<_nucleusNodes.length; i++){
					if (node == _nucleusNodes[i]){
						// Remove this node.
						_nucleusNodes[i] = null;
					}
				}
				
				// Fill the newly created slot in the bucket.
				fillBucketWithNuclei();
			}
			
	        public void nodeReleased(GrabbableLabeledNucleusNode node){
	        	System.out.println("Bucket got the release notification. " + node.getFullBoundsReference().x + "---" +
	        			node.getFullBoundsReference().y);
	        	if (node != _nodeBeingDragged){
	        		System.err.println("Error: Unexpected notification from grabbable node.");
	        	}
	        	if ( !isNodeInBucket( node ) ){
	        		// The nucleus has been dragged out of the bucket, so
	        		// notify the listeners and then forget about him.  He is
	        		// someone else's problem now.
	        		System.out.println("Nucleus removed from bucket.");
	        		notifyNucleusExtracted( node );
	        		node.removeListener(_grabbableNucleusListener);
	        	}
	        	else{
	        		System.out.println("Node released in bucket.");
	        	}
	        }
		};

		// Set up some values that will be used when filling the bucket with
		// nuclei.
		_numNucleiInMiddleLayer = (int)Math.floor(1 / NUCLEUS_WIDTH_PROPORTION);
		_numNucleiInOuterLayers = (int)((double)_numNucleiInMiddleLayer * 0.75);
		_nucleusNodes = new PNode[((2 * _numNucleiInOuterLayers) + _numNucleiInMiddleLayer)];

		fillBucketWithNuclei();
	}
	
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
	
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

	private boolean isNodeInBucket( PNode node ) {
		return (_frontOfBucketLayer.getGlobalFullBounds().intersects(node.getGlobalFullBounds()) &&
				_backOfBucketLayer.getGlobalFullBounds().intersects(node.getGlobalFullBounds()));
	}
	
	/**
	 * Add the nuclei to any open positions in the bucket, but only if the
	 * total extractable capacity has not yet been exceeded.
	 */
	private void fillBucketWithNuclei(){
		
		// Add a nucleus to any empty spots.
		for (int i = 0; i < _nucleusNodes.length; i++){
			if ( _nucleusNodes[i] == null){
				_nucleusNodes[i] = createGrabbablePoloniumNode();
				if (i < _numNucleiInOuterLayers){
					_backInteriorLayer.addChild(_nucleusNodes[i]);
				}
				else if (i < _numNucleiInOuterLayers + _numNucleiInMiddleLayer){
					_middleInteriorLayer.addChild(_nucleusNodes[i]);
				}
				else {
					_frontInteriorLayer.addChild(_nucleusNodes[i]);
				}
			}
		}

		positionNucleiInBucket();
	}
	
	private void fillBucketWithNuclei2(){
		_backInteriorLayer.addChild(createGrabbablePoloniumNode());
	}

	private void positionNucleiInBucket(){

		double nucleusWidth = NUCLEUS_WIDTH_PROPORTION * _bucketWidth;
		double nucleusOffsetX = nucleusWidth * 0.5;
		double nucleusOffsetY = -(_ellipseVerticalSpan * 0.3);

		// Add nuclei to back layer.
		for (int i = 0; i < _numNucleiInOuterLayers; i++){
			if (_nucleusNodes[i] != null){
				_nucleusNodes[i].setOffset(nucleusOffsetX, nucleusOffsetY);
			}
			nucleusOffsetX += nucleusWidth * 1.05;
		}
		
		nucleusOffsetX = nucleusWidth * 0.1;
		nucleusOffsetY = _ellipseVerticalSpan * 0.1;
		
		// Add nuclei to middle layer.
		for (int i = _numNucleiInOuterLayers; i < _numNucleiInOuterLayers + _numNucleiInMiddleLayer; i++){
			if (_nucleusNodes[i] != null){
				_nucleusNodes[i].setOffset(nucleusOffsetX, nucleusOffsetY);
			}
			nucleusOffsetX += nucleusWidth * 1.05;
		}
		
		nucleusOffsetX = nucleusWidth * 0.5;
		nucleusOffsetY = _ellipseVerticalSpan * 0.4;
		
		// Add nuclei to front layer.
		for (int i = _numNucleiInOuterLayers + _numNucleiInMiddleLayer; i < 2 * _numNucleiInOuterLayers + _numNucleiInMiddleLayer; i++){
			if (_nucleusNodes[i] != null){
				_nucleusNodes[i].setOffset(nucleusOffsetX, nucleusOffsetY);
			}
			nucleusOffsetX += nucleusWidth * 1.05;
		}
	}

	private LabeledNucleusNode createNonGrabbablePoloniumNode() {
		LabeledNucleusNode nucleusNode = new LabeledNucleusNode("Polonium Nucleus Small.png",
		        NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER, 
		        NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL, 
		        NuclearPhysicsConstants.POLONIUM_LABEL_COLOR );
		nucleusNode.scale( _bucketWidth * NUCLEUS_WIDTH_PROPORTION / nucleusNode.getFullBoundsReference().width);
		nucleusNode.setPickable(false);
		return nucleusNode;
	}
	
	private GrabbableLabeledNucleusNode createGrabbablePoloniumNode() {
		GrabbableLabeledNucleusNode nucleusNode = new GrabbableLabeledNucleusNode("Polonium Nucleus Small.png",
		        NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER, 
		        NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL, 
		        NuclearPhysicsConstants.POLONIUM_LABEL_COLOR );
		nucleusNode.scale( _bucketWidth * NUCLEUS_WIDTH_PROPORTION / nucleusNode.getFullBoundsReference().width);
		nucleusNode.setPickable(true);
		nucleusNode.addListener(_grabbableNucleusListener);
		nucleusNode.addInputEventListener(new CursorHandler());
		return nucleusNode;
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
