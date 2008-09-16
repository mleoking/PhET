package edu.colorado.phet.ohm1d.common.wire1d.forces;

import edu.colorado.phet.ohm1d.common.wire1d.Force1d;
import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;
import edu.colorado.phet.ohm1d.common.wire1d.WirePatch;
import edu.colorado.phet.ohm1d.common.wire1d.WireSystem;

/**
 * Force from the end of 'a' onto the beginning of 'b'.
 */
public class AdjacentPatchCoulombForce implements Force1d {
    WireSystem sys;
    WirePatch a;
    WirePatch b;
    CoulombForceParameters params;

    public AdjacentPatchCoulombForce( CoulombForceParameters params, WireSystem sys, WirePatch a, WirePatch b ) {
        this.a = a;
        this.b = b;
        this.sys = sys;
        this.params = params;
    }

    public double getForce( WireParticle wp ) {
        double sum = 0;
        for ( int i = 0; i < sys.numParticles(); i++ ) {
            WireParticle p = sys.particleAt( i );
            //System.err.println("i="+i+", p="+p+", wp="+wp);
            if ( p != wp ) {
                if ( p.getWirePatch() == a && wp.getWirePatch() == b ) {
                    sum += params.getForce( p.getPosition(), p.getCharge(), wp.getPosition() + a.getLength(), wp.getCharge() );
//  				//System.err.println("Different reference.");
//  				double dx=p.getPosition()-wp.getPosition()-a.getLength();
//  				double r=Math.abs(dx);
//  				if (r<minDist)
//  				    r=minDist;
//  				else if (r>maxDist)
//  				    r=maxDist;
//  				//System.err.println("P="+i);
//  				double term=k*Math.pow(r,power)*p.getCharge()*wp.getCharge();
//  				if (dx>0)
//  				    term*=-1;
//  				sum+=term;
                    //System.err.println("a="+p.getPosition()+", b="+wp.getPosition()+", sum="+sum);
                }
                else {
                    //System.err.println("Different patches.");
                }
            }
        }
        return sum;
    }
}
