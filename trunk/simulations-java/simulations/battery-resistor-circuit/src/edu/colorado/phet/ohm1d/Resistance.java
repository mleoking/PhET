package edu.colorado.phet.ohm1d;

import java.util.Vector;

import edu.colorado.phet.ohm1d.common.paint.LayeredPainter;
import edu.colorado.phet.ohm1d.common.paint.Painter;
import edu.colorado.phet.ohm1d.common.paint.SwitchablePainter;
import edu.colorado.phet.ohm1d.common.paint.particle.ParticlePainter;
import edu.colorado.phet.ohm1d.common.paint.particle.ParticlePainterAdapter;
import edu.colorado.phet.ohm1d.common.phys2d.DoublePoint;
import edu.colorado.phet.ohm1d.common.phys2d.Law;
import edu.colorado.phet.ohm1d.common.phys2d.Particle;
import edu.colorado.phet.ohm1d.common.phys2d.System2D;
import edu.colorado.phet.ohm1d.common.wire1d.WirePatch;
import edu.colorado.phet.ohm1d.gui.CoreCountListener;
import edu.colorado.phet.ohm1d.gui.ShowPainters;
import edu.colorado.phet.ohm1d.oscillator2d.Core;
import edu.colorado.phet.ohm1d.oscillator2d.Oscillate;

public class Resistance implements CoreCountListener, Law {
    double start;
    double end;
    int numCores;
    WirePatch wp;
    double amplitude;
    double freq;
    double decay;
    ParticlePainter dp;
    int coreLevelTop;
    int coreLevelBottom;
    LayeredPainter cp;
    ShowPainters showCores;
    System2D sys;

    public Resistance( double start, double end, int numCores, WirePatch wp, double amplitude, double freq, double decay, ParticlePainter dp, int coreLevelTop, int coreLevelBottom, LayeredPainter cp, ShowPainters showCores, System2D sys ) {
        this.sys = sys;
        this.showCores = showCores;
        this.cp = cp;
        this.coreLevelTop = coreLevelTop;
        this.coreLevelBottom = coreLevelBottom;
        this.dp = dp;
        this.decay = decay;
        this.freq = freq;
        this.amplitude = amplitude;
        this.wp = wp;
        this.start = start;
        this.end = end;
        this.numCores = numCores;
    }

    public void iterate( double dt, System2D sys ) {
        //relayout the cores.
        removeCores();
        painteric = new Vector();
        systemic = new Vector();
        layoutCores();
        sys.remove( this );
    }

    public void coreCountChanged( int value ) {
        if ( value != numCores ) {
            this.numCores = value;
            sys.addLaw( this );//register the update to happen synchronously.
        }
    }

    public void layoutCores() {
        double coreDX = getCoreDX();
        for ( int i = 0; i < numCores; i++ ) {
            double scalarPosition = start + ( coreDX * i ) + 15;
            if ( numCores == 1 ) {
                scalarPosition = ( end - start ) / 2 + start;
                //System.err.println("NumCores=1: end="+end+", start="+start+", scalarPos="+scalarPosition);
            }
            DoublePoint x0 = wp.getPosition( scalarPosition );
            //System.err.println("i="+i+", start="+start+", coreDX="+coreDX+", scal="+scalarPosition+", x0="+x0);
            DoublePoint axis = new DoublePoint( 1, 0 );
            //System.err.println("Made axis="+axis);
            Oscillate o = new Oscillate( x0, amplitude, freq, decay, axis );
            o.setAmplitude( 0 );
            Core core = new Core( x0, scalarPosition );
            core.setCharge( 0 );
            //core.setWirePatch(wp);
            core.setPropagator( o );
            core.setPosition( x0 );
            //cp.addPainter(new WireParticlePainter(core,dp));
            //EnergyColorPainter ecp=new EnergyColorPainter(core,255,22,20);
            ParticlePainterAdapter ppa = new ParticlePainterAdapter( dp, core );
            SwitchablePainter switcher = new SwitchablePainter( ppa ); //to allow easy switching of painters.
            painteric.add( switcher );
            if ( i % 2 == 0 ) {
                cp.addPainter( switcher, coreLevelTop );
            }
            else {
                cp.addPainter( switcher, coreLevelBottom );
            }
            showCores.add( switcher );
            sys.addParticle( core );
            systemic.add( core );
        }
    }

    Vector painteric = new Vector();
    Vector systemic = new Vector();

    public void removeCores() {
        for ( int i = 0; i < painteric.size(); i++ ) {
            Painter p = (Painter) painteric.get( i );
            cp.removePainter( p, coreLevelTop ); //!!!Because I wasn't keeping track of which it was.
            cp.removePainter( p, coreLevelBottom );
            showCores.remove( p );
        }
        for ( int i = 0; i < systemic.size(); i++ ) {
            Particle p = (Particle) systemic.get( i );
            sys.remove( p );
        }
    }

    public double getCoreDX() {
        if ( numCores <= 1 ) {
            return 0;
        }
        double coredomain = end - start;
        double coreDX = coredomain / ( numCores - 1 );
        return coreDX;
    }
}
