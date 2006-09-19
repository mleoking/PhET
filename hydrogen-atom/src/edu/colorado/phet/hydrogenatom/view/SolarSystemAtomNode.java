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


public class SolarSystemAtomNode extends AbstractAtomNode {

    public SolarSystemAtomNode() {
        super();
        
        NeutronNode neutronNode = new NeutronNode();
        ElectronNode electronNode = new ElectronNode();
       
        addChild( neutronNode );
        addChild( electronNode );
          
        OriginNode originNode = new OriginNode( Color.GREEN );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }

        neutronNode.setOffset( 0, 0 );
        electronNode.setOffset( 100, 100 );
    }
}
