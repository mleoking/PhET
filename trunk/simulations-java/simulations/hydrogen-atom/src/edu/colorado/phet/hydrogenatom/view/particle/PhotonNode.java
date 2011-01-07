// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.view.particle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.Photon;
import edu.colorado.phet.hydrogenatom.view.HAModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.util.PhotonImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * PhotonNode is the visual representation of a photon.
 * Photon images are cached to minimize memory requirements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhotonNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    /* draws an outline around the full bonds of the node */
    private static final boolean DEBUG_BOUNDS = false;
    
    /* adds an arrow to the node showing the orientation */
    private static final boolean DEBUG_ORIENTATION = false;

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------

    // public because it's used by collision detection code in the model
    public static final double DIAMETER = 30;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Photon _photon; // model element
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param photon
     */
    public PhotonNode( Photon photon ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        // Get an image from the image cache
        Image image = PhotonImageFactory.lookupPhotonImage( photon.getWavelength(), DIAMETER );
        PImage imageNode = new PImage( image );
        addChild( imageNode );
        
        // Move origin to center
        imageNode.setOffset( -imageNode.getFullBounds().getWidth()/2, -imageNode.getFullBounds().getHeight()/2 );
        
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            // puts a dot at the origin
            OriginNode originNode = new OriginNode( Color.BLACK );
            addChild( originNode );
        }
        
        if ( DEBUG_BOUNDS ) {
            // puts a rectangle around the node's bounds
            PPath diameterNode = new PPath( new Rectangle.Double( -DIAMETER/2, -DIAMETER/2, DIAMETER, DIAMETER ) );
            diameterNode.setStroke( new BasicStroke( 1f ) );
            diameterNode.setStrokePaint( Color.WHITE );
            addChild( diameterNode );
        }
        
        if ( DEBUG_ORIENTATION ) {
            // puts an arrow on the node, showing orientation
            Point2D pTail = new Point2D.Double( -DIAMETER/3, 0 );
            Point2D pHead = new Point2D.Double( DIAMETER/3, 0 );
            double headHeight = DIAMETER / 4;
            double headWidth = DIAMETER / 2;
            double tailWidth = DIAMETER / 8;
            PPath arrowNode = new ArrowNode( pTail, pHead, headHeight, headWidth, tailWidth );
            arrowNode.setPaint( photon.getColor() );
            addChild( arrowNode );
        }
        
        _photon = photon;
        _photon.addObserver( this );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update( Observable o, Object arg ) {
        update();
    }
    
    private void update() {
        setRotation( _photon.getOrientation() );
        setOffset( HAModelViewTransform.transform( _photon.getPositionRef() ) );
    }
}
