package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.*;


/*********************************************************/
/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */

/**
 * *****************************************************
 */

public class ModifiedRichardsonPropagator extends RichardsonPropagator {

    public ModifiedRichardsonPropagator( double TAU, Wave wave, Potential potential ) {
        super( TAU, wave, potential );
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
        return new ModifiedRichardsonPropagator( getEpsilon(), super.getWave(), super.getPotential() );
    }

    public void normalize() {
    }
}