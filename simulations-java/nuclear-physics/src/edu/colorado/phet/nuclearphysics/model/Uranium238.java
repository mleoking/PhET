/**
 * Class: Uranium235
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.List;

public class Uranium238 extends Nucleus {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    public static final int NUM_PROTONS = 92;
    public static final int NUM_NEUTRONS = 146;
    private static Random random = new Random();
    // The likelihood that a neutron striking a U235 nucleus will be absorbed, causing fission
    private static double ABSORPTION_PROBABILITY = 1;

    public static void setAbsoptionProbability( double probability ) {
        ABSORPTION_PROBABILITY = probability;
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private BaseModel model;

    public Uranium238( Point2D position, BaseModel model ) {
        super( position, NUM_PROTONS, NUM_NEUTRONS );
        this.model = model;
    }

    /**
     * Removes the U238 nucleus from the model and replaces it with a U239 nucleus
     * @param neutron
     */
    public void fission( Neutron neutron ) {
        if( random.nextDouble() <= ABSORPTION_PROBABILITY ) {
            Uranium239 u239 = new Uranium239( this.getPosition(), model );
            model.removeModelElement( neutron );
            this.leaveSystem();
            model.removeModelElement( this );

            model.addModelElement( u239 );

        }
    }
}
