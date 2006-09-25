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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;


public class AlphaParticleNode extends PhetPNode {

    private static final double OVERLAP = 0.333;
    private static final Stroke STROKE = new BasicStroke( 0.5f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    public AlphaParticleNode( boolean outline ) {
        super();
        
        PNode parent = new PNode();
        ProtonNode p1 = new ProtonNode();
        p1.setStroke( STROKE, STROKE_COLOR );
        ProtonNode p2 = new ProtonNode();
        p2.setStroke( STROKE, STROKE_COLOR );
        NeutronNode n1 = new NeutronNode();
        n1.setStroke( STROKE, STROKE_COLOR );
        NeutronNode n2 = new NeutronNode();
        n2.setStroke( STROKE, STROKE_COLOR );
        
        addChild( parent );
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
        
        // move the origin to the center
        double x = -( getFullBounds().getWidth() / 2 ) + ( getBounds().getX() - getFullBounds().getX() );
        double y = -( getFullBounds().getHeight() / 2 ) + ( getBounds().getY() - getFullBounds().getY() );
        parent.setOffset( x, y );
        
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            OriginNode originNode = new OriginNode( Color.GREEN );
            addChild( originNode );
        }
    }
}
