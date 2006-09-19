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

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;


public class PlumPuddingAtomNode extends AbstractAtomNode {

    public PlumPuddingAtomNode() {
        super();
        
        PImage puddingNode = PImageFactory.create( HAConstants.IMAGE_PLUM_PUDDING );
        puddingNode.scale( 0.65 );
        
        ElectronNode electronNode = new ElectronNode();
        
        addChild( puddingNode );
        addChild( electronNode );
        
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        PBounds pb = puddingNode.getFullBounds();
        puddingNode.setOffset( -pb.getWidth()/2, -pb.getHeight()/2 );
        electronNode.setOffset( 10, 10 );
    }
}
