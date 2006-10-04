/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.Color;
import java.awt.Image;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.AlphaParticle;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


public class AlphaParticleNode extends PhetPNode implements Observer {

    private static final double OVERLAP = 0.333;
    private static Image IMAGE = null;
    
    private AlphaParticle _alphaParticle;
    
    public AlphaParticleNode( AlphaParticle alphaParticle ) {
        super();
        
        Image image = createImage();
        PImage imageNode = new PImage( image );
        addChild( imageNode );
        
        // Move origin to center
        imageNode.setOffset( -imageNode.getFullBounds().getWidth()/2, -imageNode.getFullBounds().getHeight()/2 );
        
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            OriginNode originNode = new OriginNode( Color.GREEN );
            addChild( originNode );
        }
        
        _alphaParticle = alphaParticle;
        _alphaParticle.addObserver( this );
    }
        
    public static final Image createImage() {
        
        if ( IMAGE == null ) {

            PNode parent = new PNode();
            ProtonNode p1 = new ProtonNode();
            ProtonNode p2 = new ProtonNode();
            NeutronNode n1 = new NeutronNode();
            NeutronNode n2 = new NeutronNode();

            parent.addChild( p2 );
            parent.addChild( n2 );
            parent.addChild( p1 );
            parent.addChild( n1 );

            final double xOffset = ( 1 - OVERLAP ) * p1.getFullBounds().getWidth();
            final double yOffset = ( 1 - OVERLAP ) * p1.getFullBounds().getHeight();
            p1.setOffset( 0, 0 );
            p2.setOffset( xOffset, yOffset );
            n1.setOffset( xOffset, 0 );
            n2.setOffset( 0, yOffset );

            IMAGE = parent.toImage();
        }
        
        return IMAGE;
    }
    
    public void update( Observable o, Object arg ) {
        update();
    }
    
    private void update() {
        double x = _alphaParticle.getX();
        double y = _alphaParticle.getY();
        //TODO map from model to view coordinates!
        rotate( _alphaParticle.getOrientation() );
        setOffset( x, y );
    }
}
