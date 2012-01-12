// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit;

import java.util.ArrayList;
import java.util.Vector;

import edu.colorado.phet.common.phetcommon.math.DoubleSeries;
import edu.colorado.phet.batteryresistorcircuit.common.paint.gauges.IGauge;
import edu.colorado.phet.batteryresistorcircuit.common.phys2d.Law;
import edu.colorado.phet.batteryresistorcircuit.common.phys2d.System2D;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WireParticle;
import edu.colorado.phet.batteryresistorcircuit.gui.CoreCountListener;
import edu.colorado.phet.batteryresistorcircuit.gui.VoltageListener;
import edu.colorado.phet.batteryresistorcircuit.volt.CurrentListener;
import edu.colorado.phet.batteryresistorcircuit.volt.WireRegion;

/**
 * Sets the value of the gauge.
 */
public class AverageCurrent implements Law, VoltageListener, CoreCountListener {
    ArrayList al = new ArrayList();
    IGauge ig;
    DoubleSeries ds;
    WireRegion region;
    double resistance;
    double voltage;
    Vector listeners = new Vector();

    public void addCurrentListener( CurrentListener cl ) {
        this.listeners.add( cl );
    }

    public void valueChanged( double v ) {
        this.resistance = v;
    }

    public void coreCountChanged( int x ) {
        this.voltage = x;
    }

    public AverageCurrent( IGauge ig, int numPoints, WireRegion region ) {
        this.region = region;
        ds = new DoubleSeries( numPoints );
        this.ig = ig;
    }

    public void addParticle( WireParticle p ) {
        al.add( p );
    }

    public void iterate( double dt, System2D sys ) {
        double sum = 0;
        int n = 0;
        for ( int i = 0; i < al.size(); i++ ) {
            WireParticle wp = (WireParticle) al.get( i );
            if ( region.contains( wp ) ) {
                sum += wp.getVelocity() * wp.getCharge();
                n++;
            }
        }
        if ( n != 0 ) {
            sum /= n;
        }
        double hollyscale = 3.5 * 3.3;
        //double hollyscale = 2.5;
        sum = 0;//no hollywood.
        double hollywood = resistance / voltage * hollyscale;
        double total = ( sum + hollywood );
        ds.add( total );
        double display = ds.average();
        ig.setValue( display * .4 );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (CurrentListener) listeners.get( i ) ).currentChanged( display );
        }
//  	if (count++%100==0)
//  	    System.err.println("hollywood="+hollywood+", sum="+sum+", tot="+total);
    }

}
