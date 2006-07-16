/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.ParticleUnits;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 28, 2006
 * Time: 2:29:36 PM
 * Copyright (c) Jun 28, 2006 by Sam Reid
 */

public class DGParticleUnits extends ParticleUnits {
    public DGParticleUnits() {
        setHbar( new Value( 0.658, 1, "eV fs" ) );
        setMass( new Value( 0.057, 100, "eV fs^2/nm^2" ) );
        setDx( new Value( 1.0, 0.1, "nm" ) );
        setDt( new Value( 0.05, 0.10, "fs" ) );

        double s = 100.0;
        setMinVelocity( new Value( 700 / s, s, "km/s" ) );
//        setMaxVelocity( new Value( 1500 / s, s, "km/s" ) );
//        setMaxVelocity( new Value( 1500 / s * 4.0 * 7360.0 / 6000.0, s, "km/s" ) );
        setMaxVelocity( new Value( 1500 / s * 4.0 * 1840.0 / 6000.0, s, "km/s" ) );

        DecimalFormat defaultFormat = new DecimalFormat( "0" );
        setVelocityFormat( defaultFormat );

        super.setupLatticeAndRuler();
        setTimeScaleFactor( 10.0 );
    }
}
