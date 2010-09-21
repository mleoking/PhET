// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.bands;

import edu.colorado.phet.conductivity.macro.MacroSystem;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            DefaultBandSet, Band

public class InsulatorBandSet extends DefaultBandSet {

    public InsulatorBandSet( MacroSystem macrosystem, double d ) {
        super( macrosystem, d );
        double d1 = d;
        double d2 = d1 * 1.2D;
        double d3 = 0.14000000000000001D + inset * 2D;
        int i = (int) ( d3 / d2 );
        double d4 = d1 * 0.51000000000000001D;
        upper.addLevels( 0.69999999999999996D, 0.90000000000000002D, highBandNumLevels, 0.10000000000000001D - inset, d3, i, d4, 0.033333333333333333D );
        lowband.addLevels( lowBandY1, lowBandY2, lowNumLevels, 0.10000000000000001D - inset, d3, i, d4, 0.033333333333333333D );
    }

    public void initParticles() {
        super.initParticles();
        for ( int i = 0; i < getFermiLevel(); i++ ) {
            super.fillLevel( getLowerBand().energyLevelAt( i ) );
        }

    }

    public int getFermiLevel() {
        int i = super.getLowerBand().numEnergyLevels();
        return i;
    }

    public double desiredSpeedToActualSpeed( double d ) {
        return 0.0D;
    }

    public double voltageChanged( double d, double d1 ) {
        return super.voltageChanged( d, d1 );
    }
}
