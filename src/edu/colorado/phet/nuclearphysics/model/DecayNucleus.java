/**
 * Class: DecayNucleus
 * Class: edu.colorado.phet.nuclearphysics.model
 * User: Ron LeMaster
 * Date: Mar 2, 2004
 * Time: 8:14:04 AM
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;

public class DecayNucleus extends Nucleus {

    private double f = 300;
    private double dx = 60;
    private Vector2D toParentHat;

    public DecayNucleus( Nucleus coreNucleus, Vector2D toParentHat, double initPotentialEnergy ) {
        super( coreNucleus.getLocation(), coreNucleus.getNumProtons(),
               coreNucleus.getNumNeutrons(), coreNucleus.getPotentialProfile() );
        this.toParentHat = toParentHat;
        this.setPotentialEnergy( initPotentialEnergy );
    }

    public DecayNucleus( Nucleus nucleus ) {
        super( nucleus.getLocation(), nucleus.getNumProtons(), nucleus.getNumNeutrons(), nucleus.getPotentialProfile() );
    }

    public void stepInTime( double dt ) {
        this.setPotentialEnergy( Math.max( this.getPotentialEnergy() - dx, 0 ) );
        double a = f / ( this.getLocation().getX() * this.getLocation().getX() );
        Vector2D acceleration = new Vector2D( (float)a * toParentHat.getX(),
                                              (float)a * toParentHat.getY() );
        this.setAcceleration( acceleration );
        super.stepInTime( dt );
    }
}
