package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Wave;
import edu.colorado.phet.qm.model.Wavefunction;


/*********************************************************/
/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */

/**
 * *****************************************************
 */

public class ModifiedRichardsonPropagator extends RichardsonPropagator {
//    private Wavefunction last2;
//    private Wavefunction last;

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
        copy = new Wavefunction( w.getWidth(), w.getHeight() );
        stepIt( w, 0, -1 );
        stepIt( w, 0, 1 );
        stepIt( w, 1, 0 );
        stepIt( w, -1, 0 );
        applyPotential( w );
        stepIt( w, -1, 0 );
        stepIt( w, 1, 0 );
        stepIt( w, 0, -1 );
        stepIt( w, 0, 1 );
//
//        if (last2!=null){
//        dampHorizontal( w, 0, +1 );
//        dampHorizontal( w, w.getHeight() - 1, -1 );
//        dampVertical( w, 0, +1 );
//        dampVertical( w, w.getWidth() - 1, -1 );
//        }
//
//        last2 = last;
//        last = w.copy();
    }
//        private void dampHorizontal( Wavefunction w, int j, int dj ) {
//        for( int i = 0; i < w.getWidth(); i++ ) {
//            w.setValue( i, j, last2.valueAt( i, j + dj ) );
//        }
//    }
//
//    private void dampVertical( Wavefunction w, int i, int di ) {
//        for( int j = 0; j < w.getHeight(); j++ ) {
//            w.setValue( i, j, last2.valueAt( i + di, j ) );
//        }
//    }
}