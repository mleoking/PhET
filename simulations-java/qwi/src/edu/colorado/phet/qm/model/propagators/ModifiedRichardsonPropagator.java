package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;

public class ModifiedRichardsonPropagator extends RichardsonPropagator {

    public ModifiedRichardsonPropagator( double TAU, Potential potential, double hbar, double mass ) {
        super( TAU, potential, hbar, mass );
    }

    protected Complex createAlpha() {
        double epsilon = super.getEpsilon();
        return new Complex( 0.5 + 0.5 * Math.cos( epsilon / 2 ), -0.5 * Math.sin( epsilon / 2 ) );
    }

    protected Complex createBeta() {
        double epsilon = super.getEpsilon();
        return new Complex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 ), 0.5 * Math.sin( epsilon / 2 ) );
    }

    protected void prop2D( Wavefunction w ) {
        if( copy == null ) {
            copy = new Wavefunction( w.getWidth(), w.getHeight() );
        }
        stepIt( w, 0, -1 );
        stepIt( w, 0, 1 );
        stepIt( w, 1, 0 );
        stepIt( w, -1, 0 );
        applyPotential( w );
        stepIt( w, -1, 0 );
        stepIt( w, 1, 0 );
        stepIt( w, 0, -1 );
        stepIt( w, 0, 1 );
    }

    public Propagator copy() {
//        modifiedRichardsonPropagator.setDeltaTime( getDeltaTime() );
//        modifiedRichardsonPropagator.setHBar( super.getHBar() );
//        modifiedRichardsonPropagator.setMass( super.getMass() );
        return new ModifiedRichardsonPropagator(
                getDeltaTime(), super.getPotential(), getHBar(), getMass() );
    }

    public void normalize() {
    }

}