/**
 * Class: Uranium235
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.model.BaseModel;

import java.awt.geom.Point2D;

public class Uranium239 extends Nucleus {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static final int NUM_PROTONS = 92;
    public static final int NUM_NEUTRONS = 147;

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------
    
    public Uranium239( Point2D position, BaseModel model ) {
        super( position, NUM_PROTONS, NUM_NEUTRONS );
    }
}
