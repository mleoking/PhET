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

public class Uranium238 extends Nucleus {

    private BaseModel model;

    public Uranium238( Point2D.Double position, BaseModel model ) {
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
                if( neutron.getLocation().distanceSq( this.getLocation() )
                    < this.getRadius() * this.getRadius() ) {
                    this.fission( neutron );
                }
            }
        }
    }

    public void fission( Neutron neutron ) {
        // Move the neutron way, way away so it doesn't show and doesn't
        // cause another fission event. It will be destroyed later.
        neutron.setLocation( 100E3, 100E3 );
        neutron.setVelocity( 0, 0 );
    }
}
