/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

/**
 * See: http://www.mtnmath.com/whatth/node47.html
 */

public class ClassicalWavePropagator {
    private Lattice2D last2;
    private Lattice2D last;
    private Potential potential;

    public ClassicalWavePropagator( Potential potential ) {
        this.potential = potential;
    }

    public void initialize( Lattice2D last, Lattice2D last2 ) {
        this.last2 = last2;
        this.last = last;
    }

    public void addInitialization( Lattice2D last, Lattice2D last2 ) {
        if( this.last2 == null ) {
            this.last2 = last2.createEmptyWavefunction();
        }
        if( this.last == null ) {
            this.last = last.createEmptyWavefunction();
        }
        this.last2.add( last2 );
        this.last.add( last );
    }

    public void propagate( Lattice2D w ) {
        float neigh = 0.0f;
        if( last == null ) {
            last = w.copy();
            last2 = w.copy();
            return;
        }
        float c = 0.5f;
        float cSquared = c * c;
        int width = w.getWidth() - 1;
        int height = w.getHeight() - 1;
        for( int i = 1; i < width; i++ ) {
            for( int j = 1; j < height; j++ ) {
                //todo, use knowledge of the potential barrier geometry for iteration here
                if( potential.getPotential( i, j, 0 ) != 0 ) {
                    w.setValue( i, j, 0 );
                }
                else {
                    neigh = cSquared * ( last.wavefunction[i + 1][j] + last.wavefunction[i - 1][j] + last.wavefunction[i][j + 1] + last.wavefunction[i][j - 1] + last.wavefunction[i][j] * -4 );
                    w.setValue( i, j, last.wavefunction[i][j] * 2 - last2.wavefunction[i][j] + neigh );
                }
            }
        }

        dampHorizontal( w, 0, +1 );
        dampHorizontal( w, w.getHeight() - 1, -1 );
        dampVertical( w, 0, +1 );
        dampVertical( w, w.getWidth() - 1, -1 );

//        for( int i = 0; i < 10; i++ ) {
//            dampScale( w );
//        }
        //todo avoid copying by cycling references.?
        last.copyTo( last2 );
        clearPotential( last2 );   //todo maybe only need to do one here.
        w.copyTo( last );
        clearPotential( last );
//        w.setMagnitudeDirty();
//        last.setMagnitudeDirty();
//        last2.setMagnitudeDirty();
    }


    private void dampScale( Lattice2D lattice ) {
        int dampSize = 20;
        for( int i = 0; i < lattice.getWidth(); i++ ) {
            for( int j = 0; j < lattice.getHeight(); j++ ) {
                if( i < dampSize || j < dampSize || i > lattice.getWidth() - dampSize || j > lattice.getHeight() - dampSize )
                {
                    float damp = 1.0f;
                    if( j < dampSize ) {
                        damp = getDamp( dampSize - j );
                    }
                    if( j > lattice.getHeight() - dampSize ) {
                        int distToWall = lattice.getHeight() - j;
                        damp = getDamp( dampSize - distToWall );
                    }
                    if( i < dampSize ) {
                        damp = getDamp( dampSize - i );
                    }
                    if( i > lattice.getWidth() - dampSize ) {
                        int distToWall = lattice.getWidth() - i;
                        damp = getDamp( dampSize - distToWall );
                    }
                    lattice.setValue( i, j, lattice.getValue( i, j ) * damp );
                    last.setValue( i, j, last.getValue( i, j ) * damp );
                    last2.setValue( i, j, last2.getValue( i, j ) * damp );
                }
            }
        }
    }

    private float getDamp( int depthInDampRegion ) {
        return (float)( 1 - depthInDampRegion * 0.0002 );
    }

    private void clearPotential( Lattice2D last ) {
        //todo iterate over the wave, and where the potential is nonzero, set the lattice value to 0. (18% application in one run)
        for( int i = 0; i < last.getWidth(); i++ ) {
            for( int j = 0; j < last.getHeight(); j++ ) {
                if( potential.getPotential( i, j, 0 ) != 0.0 ) {
                    last.setValue( i, j, 0.0f );
                }
            }
        }
    }

    private Potential getPotential() {
        return potential;
    }

    private void dampHorizontal( Lattice2D lattice2D, int j, int dj ) {
        for( int i = 0; i < lattice2D.getWidth(); i++ ) {
            lattice2D.setValue( i, j, last2.getValue( i, j + dj ) );
        }
    }

    private void dampVertical( Lattice2D lattice2D, int i, int di ) {
        for( int j = 0; j < lattice2D.getHeight(); j++ ) {
            lattice2D.setValue( i, j, last2.getValue( i + di, j ) );
        }
    }

//    private void dampHorizontal( Lattice2D lattice2D, int j, int dj ) {
//        int w=6;
//        for( int i = w; i < lattice2D.getWidth()-w; i++ ) {
//            float value=0;
//            int sum=0;
//            for (int k=-w;k<=w;k++){
//                value+=last2.getValue(i+k,j+dj );
//                sum++;
//            }
////            float value = last2.getValue( i, j + dj )*2+last2.getValue( i-1, j + dj )+last2.getValue( i+1, j + dj );
//            lattice2D.setValue( i, j, value/sum );
//        }
//    }
//
//    private void dampVertical( Lattice2D lattice2D, int i, int di ) {
//        for( int j = 1; j < lattice2D.getHeight()-1; j++ ) {
//            float value = last2.getValue( i + di, j )*2+last2.getValue( i + di, j-1 )+last2.getValue( i + di, j+1 );
//            lattice2D.setValue( i, j, value/4f );
//        }
//    }

//    private float ZERO = 0;

//    private float last( int i, int j ) {
//        return last.wavefunction[i][j];
//        if( potential.getPotential( i, j, 0 ) == 0 ) {
//            return last.wavefunction[i][j];
//        }
//        else {
//            return 0;
//        }
//    }

    public void setDeltaTime( float deltaTime ) {
    }

    public float getSimulationTime() {
        return 0;
    }

    public void reset() {
        last2 = null;
        last = null;
    }

    public void setBoundaryCondition( int i, int k, float value ) {
        if( last != null ) {
            last.setValue( i, k, value );
        }
        if( last2 != null ) {
            last2.setValue( i, k, value );
        }
    }

//    public Propagator copy() {
//        return new ClassicalWavePropagator( getPotential() );
//    }

//    public void normalize() {
//        if( last2 != null ) {
//            last2.normalize();
//        }
//        if( last != null ) {
//            last.normalize();
//        }
//    }

//    public void setWavefunctionNorm( float norm ) {
//        if( last2 != null ) {
//            last2.setMagnitude( norm );
//        }
//        if( last != null ) {
//            last.setMagnitude( norm );
//        }
//    }

    public void scale( float scale ) {
        if( last2 != null ) {
            last2.scale( scale );
        }
        if( last != null ) {
            last.scale( scale );
        }
    }

    public Lattice2D getLast() {
        return last;
    }

    public Lattice2D getLast2() {
        return last2;
    }

//    public void copyTo( int i, int j, Propagator dst ) {
//        super.copyTo( i, j, dst );
//
//        if( dst instanceof ClassicalWavePropagator ) {
//            ClassicalWavePropagator classicalDst = (ClassicalWavePropagator)dst;
//            if( last2 != null && classicalDst.last2 != null ) {
//                float val2 = last2.valueAt( i, j );
//                classicalDst.last2.setValue( i, j, val2 );
//            }
//            if( last != null && classicalDst.last != null ) {
//                float val = last.valueAt( i, j );
//                classicalDst.last.setValue( i, j, val );
//            }
//        }
//        else {
//            throw new RuntimeException( "Tried to copy Classical to wrong propagator type: " + dst.getClass().getName() );
//        }
//    }

//    public void clearWave( Rectangle rect ) {
//        super.clearWave( rect );
//
//        if( last2 != null ) {
//            last2.clearRect( rect );
//        }
//        if( last != null ) {
//            last.clearRect( rect );
//        }
//    }

//    public void splitWave( Rectangle region, Propagator a, Propagator b ) {
//        super.splitWave( region, a, b );
//
//        if( a instanceof ClassicalWavePropagator && b instanceof ClassicalWavePropagator ) {
//            ClassicalWavePropagator ca = (ClassicalWavePropagator)a;
//            ClassicalWavePropagator cb = (ClassicalWavePropagator)b;
//            if( last2 != null && ca.last2 != null && cb.last2 != null ) {
//                last2.splitWave( region, ca.last2, cb.last2 );
//            }
//            if( last != null && ca.last != null && cb.last != null ) {
//                last.splitWave( region, ca.last, cb.last );
//            }
//        }
//        else {
//            throw new RuntimeException( "Tried to split Classical to wrong propagator type: a=" + a.getClass().getName() + ", b=" + b.getClass().getName() );
//        }
//    }
//
//    public void combineWaves( Rectangle region, Propagator a, Propagator b ) {
//        super.combineWaves( region, a, b );
//        if( a instanceof ClassicalWavePropagator && b instanceof ClassicalWavePropagator ) {
//            ClassicalWavePropagator ca = (ClassicalWavePropagator)a;
//            ClassicalWavePropagator cb = (ClassicalWavePropagator)b;
//            if( last2 != null && ca.last2 != null && cb.last2 != null ) {
//                last2.combineWaves( region, ca.last2, cb.last2 );
//            }
//            if( last != null && ca.last != null && cb.last != null ) {
//                last.combineWaves( region, ca.last, cb.last );
//            }
//        }
//        else {
//            throw new RuntimeException( "Tried to combine Classical to wrong propagator type: a=" + a.getClass().getName() + ", b=" + b.getClass().getName() );
//        }
//    }

    public void setValue( int i, int j, float real, float imag ) {
        if( last != null ) {
            last.setValue( i, j, real );
        }
        if( last2 != null ) {
            last2.setValue( i, j, real );
        }
    }

    public void setSize( int width, int height ) {
        if( last != null ) {
            last.setSize( width, height );
        }
        if( last2 != null ) {
            last2.setSize( width, height );
        }
    }

    public void setPotential( Potential potential ) {
        this.potential = potential;
    }

    public void clear() {
        if( last != null ) {
            last.clear();
        }
        if( last2 != null ) {
            last2.clear();
        }
    }
}
