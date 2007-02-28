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

    public void propagate( Lattice2D w ) {
        float neigh = 0.0f;
        if( last == null || last2 == null || !w.getSize().equals( last.getSize() ) || !w.getSize().equals( last2.getSize() ) )
        {
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

        //todo avoid copying by cycling references.?
        last.copyTo( last2 );
        w.copyTo( last );
        //        clearPotential( last2 );   //todo maybe only need to do one here.
//        clearPotential( last );
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

    public void setBoundaryCondition( int i, int k, float value ) {
        if( last != null ) {
            last.setValue( i, k, value );
        }
        if( last2 != null ) {
            last2.setValue( i, k, value );
        }
    }

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

    public Potential getPotential() {
        return potential;
    }
}
