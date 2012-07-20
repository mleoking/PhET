// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model.waves;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.quantumwaveinterference.model.Wave;
import edu.colorado.phet.quantumwaveinterference.model.WaveSetup;
import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.colorado.phet.quantumwaveinterference.model.math.Complex;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:16:39 AM
 */

public class GaussianWave2D extends WaveSetup implements Wave {
    private GaussianWave1D xWave;
    private GaussianWave1D yWave;
    private double globalPhase;
    private double scale = 1.0;

    public GaussianWave2D( Point2D center, MutableVector2D momentum, double dxLattice, double hbar ) {
        this( center, momentum, dxLattice, dxLattice, hbar );
    }

    public GaussianWave2D( Point2D center, MutableVector2D momentum, double dxLattice, double dyLattice, double hbar ) {
        this.xWave = new GaussianWave1D( momentum.getX(), center.getX(), dxLattice, hbar );
        this.yWave = new GaussianWave1D( momentum.getY(), center.getY(), dyLattice, hbar );
        super.setWave( this );
    }

    public void initialize( Wavefunction wavefunction, double time ) {
        super.initialize( wavefunction, time );
        wavefunction.normalize();
        wavefunction.scale( scale );
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        Complex x = xWave.getValue( i );
        Complex y = yWave.getValue( j );
        Complex product = x.times( y );
        product = product.times( Complex.exponentiateImaginary( globalPhase ) );//rotate through global phase offset
        return product;
    }

    public void setPhase( double globalPhase ) {
        this.globalPhase = globalPhase;
    }

    public void setScale( double scale ) {
        this.scale = scale;
    }
}
