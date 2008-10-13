/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.ParticleUnits;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 28, 2006
 * Time: 2:29:36 PM
 */

public class DGParticleUnits extends ParticleUnits {
    public DGParticleUnits() {
        setHbar( new Value( 0.658, 1, QWIResources.getString( "ev.fs" ) ) );
        setMass( new Value( 0.057, 100, QWIResources.getString( "ev.fs.2.nm.2" ) ) );
        setDx( new Value( 1.0, 0.1, QWIResources.getString( "nm" ) ) );
        setDt( new Value( 0.05, 0.10, QWIResources.getString( "fs" ) ) );

        double s = 100.0;
        setMinVelocity( new Value( 700 / s, s, QWIResources.getString( "gun.km-s" ) ) );
//        setMaxVelocity( new Value( 1500 / s, s, "km/s" ) );
//        setMaxVelocity( new Value( 1500 / s * 4.0 * 7360.0 / 6000.0, s, "km/s" ) );
        setMaxVelocity( new Value( 1500 / s * 4.0 * 1840.0 / 6000.0, s, QWIResources.getString( "gun.km-s" ) ) );

        DecimalFormat defaultFormat = new DecimalFormat( "0" );
        setVelocityFormat( defaultFormat );

        super.setupLatticeAndRuler();
        setTimeScaleFactor( 10.0 );
    }
}
