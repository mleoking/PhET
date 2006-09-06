/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import edu.colorado.phet.hydrogenatom.view.HANode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;


public abstract class AbstractEnergyDiagram extends HANode {

    public AbstractEnergyDiagram( String imageName ) {
        super();
        
        PImage image = PImageFactory.create( imageName );
        addChild( image );
    }
}
