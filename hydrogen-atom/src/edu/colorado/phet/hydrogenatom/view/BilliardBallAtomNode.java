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

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;


public class BilliardBallAtomNode extends HANode {

    public BilliardBallAtomNode() {
        PImage image = PImageFactory.create( HAConstants.IMAGE_BILLIARD_BALL_ATOM );
        addChild( image );
    }
}
