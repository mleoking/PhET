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

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


public class AlphaParticleNode extends PhetPNode {

    private static final double OVERLAP = 0.333;
    private static Image IMAGE = null;
    
    public AlphaParticleNode() {
        super();
        if ( IMAGE == null ) {
            IMAGE = createImage();
        }
        PImage imageNode = new PImage( IMAGE );
        addChild( imageNode );
        
        // Move origin to center
        imageNode.setOffset( -imageNode.getFullBounds().getWidth()/2, -imageNode.getFullBounds().getHeight()/2 );
        
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            OriginNode originNode = new OriginNode( Color.GREEN );
            addChild( originNode );
        }
    }
        
    private static final Image createImage() {
        
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
        
        return parent.toImage();
    }
}
