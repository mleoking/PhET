/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

/**
 * See: http://www.mtnmath.com/whatth/node47.html
 */

public class DampedClassicalWavePropagator extends ClassicalWavePropagator {
    private int dampX;
    private int dampY;
    private Lattice2D largeLattice;

    public DampedClassicalWavePropagator( Potential potential, int dampX, int dampY ) {
        super( potential );
        this.dampX = dampX;
        this.dampY = dampY;
    }

    public Lattice2D getLargeLattice() {
        return largeLattice;
    }

    public int getDampX() {
        return dampX;
    }

    public int getDampY() {
        return dampY;
    }

    class PaddedPotential implements Potential {
        private Potential potential;

        public PaddedPotential( Potential potential ) {
            this.potential = potential;
        }

        public double getPotential( int i, int j, int time ) {
            return potential.getPotential( i - dampX, j - dampY, time );
        }
    }

    public void setPotential( Potential potential ) {
        super.setPotential( new PaddedPotential( potential ) );
    }

    public void setBoundaryCondition( int i, int k, float value ) {
        super.setBoundaryCondition( i + dampX, k + dampY, value );
    }

    public void clearOffscreenLatticeValue( int i, int k ) {
        largeLattice.setValue( i, k, 0 );
        super.setBoundaryCondition( i, k, 0 );
    }

    public void propagate( Lattice2D w ) {
        //copy to large lattice
        if( largeLattice == null || largeLattice.getWidth() != w.getWidth() || largeLattice.getHeight() != w.getHeight() )
        {
            largeLattice = new Lattice2D( w.getWidth() + dampX * 2, w.getHeight() + dampY * 2 );
        }
        for( int i = 0; i < w.getWidth(); i++ ) {
            for( int k = 0; k < w.getHeight(); k++ ) {
                largeLattice.setValue( i + dampX, k + dampY, w.getValue( i, k ) );
            }
        }
        super.propagate( largeLattice );
        for( int i = 0; i < 1; i++ ) {
            dampScale( largeLattice );
        }
        for( int i = 0; i < w.getWidth(); i++ ) {
            for( int k = 0; k < w.getHeight(); k++ ) {
                w.setValue( i, k, largeLattice.getValue( i + dampX, k + dampY ) );//+dampX*2,k+dampY*2) );
            }
        }
    }

    private void dampVertical( Lattice2D lattice, int origin, int sign, int numDampPts ) {
        for( int j = 0; j < lattice.getHeight(); j++ ) {
            for( int step = 0; step < numDampPts; step++ ) {
                int distFromDampBoundary = numDampPts - step;
                float damp = getDamp( distFromDampBoundary );
                int i = origin + step * sign;
                lattice.setValue( i, j, lattice.getValue( i, j ) * damp );
                getLast().setValue( i, j, getLast().getValue( i, j ) * damp );
//                getLast2().setValue( i, j, getLast2().getValue( i, j ) * damp );
            }
        }
    }

//    private void dampHorizontal( Lattice2D lattice, int origin, int sign, int numDampPts ) {
//        for( int i = 0; i < lattice.getWidth(); i++ ) {
//            for( int step = 0; step < numDampPts; step++ ) {
//                int distFromDampBoundary = numDampPts - step;
//                float damp = getDamp( distFromDampBoundary );
//                int j = origin + step * sign;
//                lattice.setValue( j, i, lattice.getValue( j, i ) * damp );
//                getLast().setValue( j, i, getLast().getValue( j, i ) * damp );
////                getLast2().setValue( j, i, getLast2().getValue( j, i ) * damp );
//            }
//        }
//    }

    private void dampHorizontal( Lattice2D lattice, int origin, int sign, int numDampPts ) {
        for( int i = 0; i < lattice.getWidth(); i++ ) {
            for( int step = 0; step < numDampPts; step++ ) {
                int distFromDampBoundary = numDampPts - step;
                float damp = getDamp( distFromDampBoundary );
                int j = origin + step * sign;
                lattice.setValue( i, j, lattice.getValue( i, j ) * damp );
                getLast().setValue( i, j, getLast().getValue( i, j ) * damp );
//                getLast2().setValue( j, i, getLast2().getValue( j, i ) * damp );
            }
        }
    }

//    private void dampScale( Lattice2D lattice ) {
//        dampVertical( lattice, 0, +1, dampX / 2 );
//        dampVertical( lattice, lattice.getWidth() - 1, -1, dampX / 3 );
//        dampHorizontal( lattice, 0, +1, dampY / 2 );
//        dampHorizontal( lattice, lattice.getHeight() - 1, -1, dampY / 3 );
//    }

//    private void dampScale( Lattice2D lattice ) {
//        dampVertical( lattice, 0, +1, dampX / 2 );
//        dampVertical( lattice, lattice.getWidth() - 1, -1, dampX / 3 );
//        dampHorizontal( lattice, 0, +1, dampY / 2 );
//        dampHorizontal( lattice, lattice.getHeight() - 1, -1, dampY / 3 );
//    }

    private void dampScale( Lattice2D lattice ) {
        dampVertical( lattice, 0, +1, dampY / 2 );
        dampVertical( lattice, lattice.getWidth() - 1, -1, dampY / 2 );
        dampHorizontal( lattice, 0, +1, dampX / 2 );
        dampHorizontal( lattice, lattice.getHeight() - 1, -1, dampX / 2 );
    }

//    double[]dampCoefficients=new double[]{0.999,0.999,0.998,0.995,0.99,0.9,0.8};

    private float getDamp( int depthInDampRegion ) {
        return (float)( 1 - depthInDampRegion * 0.0001 );
    }

    double[] dampCoefficients = new double[]{0.999, 0.999, 0.998, 0.995, 0.99, 0.95, 0.9, 0.8, 0.7, 0.5, 0.25, 0.2};
//    private float getDamp( int depthInDampRegion ) {
//
//        return (float)dampCoefficients[depthInDampRegion];
//    }

}
