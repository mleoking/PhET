/**
 * Class: Neutron
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Mar 7, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.nuclearphysics.Config;

import java.awt.geom.Point2D;

public class Neutron extends NuclearParticle {

    public Neutron( Point2D location ) {
        super( location );
        setVelocity( (float)Config.neutronSpeed, 0 );
    }

    public Neutron( Point2D location, double theta ) {
        super( location );
        setVelocity( theta );
    }

    public void setVelocity( double theta ) {
        setVelocity( (float)( Math.cos( theta ) * Config.neutronSpeed ),
                     (float)( Math.sin( theta ) * Config.neutronSpeed ) );
    }

    public void setPosition( double v, double v1 ) {
        if( v == 100000 ) {
            System.out.println( "!!!" );
        }
        super.setPosition( v, v1 );
    }
}
