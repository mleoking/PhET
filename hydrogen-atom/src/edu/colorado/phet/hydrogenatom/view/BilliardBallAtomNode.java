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
import java.awt.Dimension;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;


public class BilliardBallAtomNode extends AbstractAtomNode {

    public BilliardBallAtomNode() {
        super();
        
        PImage billiardBallNode = PImageFactory.create( HAConstants.IMAGE_BILLIARD_BALL );
        addChild( billiardBallNode );
        
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        billiardBallNode.setOffset( -billiardBallNode.getFullBounds().getWidth()/2, -billiardBallNode.getFullBounds().getHeight()/2 );
    }
}
