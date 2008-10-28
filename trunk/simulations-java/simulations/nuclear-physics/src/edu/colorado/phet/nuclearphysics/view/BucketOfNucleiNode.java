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

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayModel.Listener;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

public class BucketOfNucleiNode extends PNode {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	public static final Stroke LINE_STROKE = new BasicStroke( 0.5f );
	public static final Color OUTER_COLOR_DARK = new Color (0xAA7700);
	public static final Color OUTER_COLOR_LIGHT = new Color (0xFF9933);
	public static final Color INNER_COLOR_DARK = new Color (0xAA7700);
	public static final Color INNER_COLOR_LIGHT = new Color (0xCC9933);
	public static final double NUCLEUS_WIDTH_PROPORTION = 0.3;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private ArrayList _listeners = new ArrayList();
    private PNode _backLayer;
    private PNode _middleLayer;
    private PNode _frontLayer;
    private GrabbableLabeledNucleusNode.Listener _grabbableNucleusListener;
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
	/**
	 * Constructor - This takes a width and height in world coordinates.
	 */
	public BucketOfNucleiNode(double width, double height){
		
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
		_backLayer = new PNode();
		addChild( _backLayer );
		_middleLayer = new PNode();
		addChild( _middleLayer );
		_frontLayer = new PNode();
		addChild( _frontLayer );
		
		double ellipseVerticalSpan = height * 0.4;
		
		// Draw the inside of the bucket.
		PhetPPath ellipseInBackOfBucket = new PhetPPath(new Ellipse2D.Double( 0, 0, width, ellipseVerticalSpan ));
		ellipseInBackOfBucket.setStroke( LINE_STROKE );
		ellipseInBackOfBucket.setPaint(innerPaint);
		_backLayer.addChild(ellipseInBackOfBucket);

		// Draw the outside of the bucket.
		GeneralPath bucketBodyPath = new GeneralPath();
		bucketBodyPath.moveTo(0, (float)(ellipseVerticalSpan / 2));
		bucketBodyPath.lineTo((float)(width * 0.1), (float)(height * 0.8));
		bucketBodyPath.quadTo((float)(width / 2), (float)(height * 1.1), (float)(width * 0.9), (float)(height * 0.8));
		bucketBodyPath.lineTo((float)(width), (float)(ellipseVerticalSpan / 2));
		bucketBodyPath.quadTo((float)(width / 2), (float)(height * 0.6), 0, (float)(ellipseVerticalSpan / 2));
		bucketBodyPath.closePath();
		PhetPPath bucketBody = new PhetPPath( bucketBodyPath );
		bucketBody.setStroke( LINE_STROKE );
		bucketBody.setPaint(outerPaint);
		_frontLayer.addChild(bucketBody);
		
		// Draw the handle.
		PhetPPath bucketHandle = new PhetPPath( new QuadCurve2D.Double(width/2, ellipseVerticalSpan, width * 1.6, 
				ellipseVerticalSpan * 2, width, ellipseVerticalSpan / 2) );
		bucketHandle.setStroke( LINE_STROKE );
		_frontLayer.addChild(bucketHandle);
		
		// Create the listener object that will be registered with the
		// grabbable nuclei.  We do this in a non-anonymous way so that we can
		// unregister later.
		_grabbableNucleusListener = new GrabbableLabeledNucleusNode.Listener(){
	        public void nodeReleased(GrabbableLabeledNucleusNode node){
	        	System.out.println("Bucket got the release notification. " + node.getFullBoundsReference().x + "---" +
	        			node.getFullBoundsReference().y);
	        	if ( !isNodeInBucket( node ) ){
	        		// The nucleus has been dragged out of the bucket, so
	        		// notify the listeners and then forget about him.  He is
	        		// someone else's problem now.
	        		System.out.println("Nucleus removed from bucket.");
	        		notifyNucleusExtracted( node );
	        		node.removeListener(_grabbableNucleusListener);
	        	}
	        	else{
	        		System.out.println("Node DOES intersect");
	        	}
	        }
		};
		
		GrabbableLabeledNucleusNode nucleusNode = new GrabbableLabeledNucleusNode("Polonium Nucleus Small.png",
                NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER, 
                NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL, 
                NuclearPhysicsConstants.POLONIUM_LABEL_COLOR );
		nucleusNode.scale( width * NUCLEUS_WIDTH_PROPORTION / nucleusNode.getFullBoundsReference().width);
		_middleLayer.addChild(nucleusNode);
		nucleusNode.addListener(_grabbableNucleusListener);
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
		return (_frontLayer.getGlobalFullBounds().intersects(node.getGlobalFullBounds()) &&
				_backLayer.getGlobalFullBounds().intersects(node.getGlobalFullBounds()));
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
