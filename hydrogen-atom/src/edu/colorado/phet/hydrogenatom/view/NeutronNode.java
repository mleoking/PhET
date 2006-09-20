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
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;


public class NeutronNode extends PhetPNode {

    private static final double DESIRED_DIAMETER = 11;
    
    public NeutronNode() {
        super();

        PImage imageNode = PImageFactory.create( HAConstants.IMAGE_NEUTRON );
        addChild( imageNode );
        
        double imageDiameter = imageNode.getFullBounds().getWidth();
        double scale = DESIRED_DIAMETER / imageDiameter;
        double x = scale * -( imageNode.getFullBounds().getWidth() / 2 );
        double y = scale * -( imageNode.getFullBounds().getHeight() / 2 );
        imageNode.translate( x, y );
        imageNode.scale( scale );
    }
}
