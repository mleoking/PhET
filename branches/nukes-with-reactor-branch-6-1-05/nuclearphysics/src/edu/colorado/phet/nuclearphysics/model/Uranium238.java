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

public class Uranium238 extends Nucleus {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

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
        super( position, 92, 146 );
        this.model = model;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Check to see if we are being hit by a neutron
        for( int i = 0; i < model.numModelElements(); i++ ) {
            ModelElement me = model.modelElementAt( i );
            if( me instanceof Neutron ) {
                Neutron neutron = (Neutron)me;
                if( neutron.getPosition().distanceSq( this.getPosition() )
                    < this.getRadius() * this.getRadius() ) {
                    this.fission( neutron );
                }
            }
        }
    }

    public void fission( Neutron neutron ) {
        if( random.nextDouble() <= ABSORPTION_PROBABILITY ) {
            // Move the neutron way, way away so it doesn't show and doesn't
            // cause another fission event. It will be destroyed later. Do it
            // twice so the neutron's previous position gets set to the same thing.
            neutron.setPosition( 100E3, 100E3 );
            neutron.setPosition( 100E3, 100E3 );
            neutron.setVelocity( 0, 0 );
        }
    }
}
