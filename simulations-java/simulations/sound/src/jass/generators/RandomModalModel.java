// Copyright 2002-2011, University of Colorado
package jass.generators;


/**
 * Modal model, with random modes.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class RandomModalModel extends ModalModel {

    /**
     * Constructor. Create modes uniformly for range chosen. Material
     * model constant mmc determines damping by d = mmc * f
     *
     * @param nf     number of modes.
     * @param np     number of locations.
     * @param fmin   minimum frequeny
     * @param fmax   maximum frequeny
     * @param mmcmin minimum material const.
     * @param mmcmax maximum material const.
     * @param ammin  minimum gain
     * @param amax   maximum gain
     */
    public RandomModalModel( int nf, int np, float fmin, float fmax, float mmcmin, float mmcmax, float amin, float amax ) {
        super( nf, np );
        randomize( fmin, fmax, mmcmin, mmcmax, amin, amax );
    }

    private void randomize( float fmin, float fmax, float mmcmin, float mmcmax, float amin, float amax ) {
        float x = (float)Math.random();
        float mmc = (float)( mmcmin + x * ( mmcmax - mmcmin ) );
        for( int i = 0; i < nf; i++ ) {
            x = (float)Math.random();
            f[i] = fmin + x * ( fmax - fmin );
            d[i] = (float)( mmc * f[i] );
            for( int k = 0; k < np; k++ ) {
                float y = (float)Math.random();
                a[k][i] = amin + y * ( amax - amin );
            }
        }
    }

}


