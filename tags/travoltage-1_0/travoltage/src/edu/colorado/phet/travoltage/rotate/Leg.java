package edu.colorado.phet.travoltage.rotate;

import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.utils.TruncatedSeries;
import edu.colorado.phet.travoltage.CompositeParticleContainer;
import edu.colorado.phet.travoltage.ShockElectronFactory;

import java.util.Vector;

public class Leg implements AngleListener {
    TruncatedSeries ts;
    ShockElectronFactory fact;
    CompositeParticleContainer cpc;

    public Leg( ShockElectronFactory fact, CompositeParticleContainer cpc ) {
        this.fact = fact;
        this.cpc = cpc;
        ts = new TruncatedSeries( 10 );
    }

    public void angleChanged( double ang ) {
        ts.add( new Double( ang ) );
        Vector last = ts.get();
        double avgVel = 0;
        double avgAng = 0;
        for( int i = 0; i < last.size() - 1; i++ ) {
            avgAng += ( (Double)last.get( i ) ).doubleValue();
            avgVel += ( (Double)last.get( i ) ).doubleValue() - ( (Double)last.get( i + 1 ) ).doubleValue();
        }
        //edu.colorado.phet.common.util.Debug.traceln("Avg vel="+avgVel+", avg angle="+avgAng);
        if( avgAng > 0 & avgAng < 10 && Math.abs( avgVel ) > .3 ) {
            //edu.colorado.phet.common.util.Debug.traceln("Add electrons!!");
            Particle p = fact.newParticle();
            cpc.add( p );
            edu.colorado.phet.common.util.ThreadHelper.quietNap( 15 );
        }
    }
}
