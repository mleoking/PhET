/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.view.gun.AbstractGunNode;
import edu.colorado.phet.quantumwaveinterference.view.gun.DefaultGunParticle;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:34:12 PM
 */

public class DGParticle extends DefaultGunParticle {
    private double covariance = 0.06;
    private double startYFraction = 0.75;

    public DGParticle( AbstractGunNode gunNode ) {
        super( gunNode, QWIResources.getString( "particles.electrons" ), "quantum-wave-interference/images/electron-thumb.png", new DGParticleUnits() );
    }

    protected double getStartDxLattice() {
        return covariance * getDiscreteModel().getGridWidth();
    }

    protected double getStartY() {
        return getDiscreteModel().getGridHeight() * startYFraction;
    }

    public double getCovariance() {
        return covariance;
    }

    public void setCovariance( double covariance ) {
        this.covariance = covariance;
    }

    public double getStartYFraction() {
        return startYFraction;
    }

    public void setStartYFraction( double startYFraction ) {
        this.startYFraction = startYFraction;
    }

}
