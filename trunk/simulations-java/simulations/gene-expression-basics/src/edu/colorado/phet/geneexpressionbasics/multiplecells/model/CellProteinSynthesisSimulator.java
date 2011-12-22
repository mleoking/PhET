// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.util.Random;

/**
 * This class defines a synthetic cell.  The central dogma is simulated as a
 * Markov process for a single protein.
 * <p/>
 * Transcription  Translation
 * DNA   ->    RNA       ->    Protein
 * <p/>
 * Simulated using the algorithm from Gillespie, 1977
 *
 * @Author George A. Emanuel
 */

public class CellProteinSynthesisSimulator {

    private Random _random = new Random();
    private double _timeStep = 5e2;
    private double _currentTime;

    private int[] _objectCounts = { 20, //gene count
            2000, //free transcription factor count
            5000, //polymerase count
            0, //gene, transcription factor complex count
            0, //gene, TF, polymerase count
            0, //mRNA count
            2000, //ribosome count
            0, //mRNA, ribosome complex count
            0 //protein count
    };

    private double[] _reactionProbabilities = { 0.001f, //gene, TF association
            0.0009f, //gene-TF degradation
            0.001f, //gene-TF-polymerase association
            0.00085f, //gene-TF-polymerase degradation
            0.003f, //transcription
            0.001f, //mRNA-ribosome association
            0.0009f, //mRNA-ribosome degradation
            0.0009f, //translation
            0.0003f //protein degradation
    };

    public CellProteinSynthesisSimulator() {
        _currentTime = 0.0;
    }

    public CellProteinSynthesisSimulator( int ribosomeCount ) {
        _objectCounts[6] = ribosomeCount;
        System.out.println( ribosomeCount );
        _currentTime = 0.0;
    }

    /**
     * Sets the number of transcription factors
     *
     * @param tfCount number of transcription factors
     */
    public void setTranscriptionFactorCount( int tfCount ) {
        _objectCounts[1] = tfCount;
    }

    /**
     * Sets the number of polymerases
     *
     * @param polymeraseCount number of polymerases
     */
    public void setPolymeraseCount( int polymeraseCount ) {
        _objectCounts[2] = polymeraseCount;
    }

    /**
     * Sets the rate that transcription factors associate with genes
     *
     * @param newRate
     */
    public void setGeneTranscriptionFactorAssociationRate( double newRate ) {
        _reactionProbabilities[0] = newRate;
    }

    /**
     * Sets the rate constant for the polymerase to bind to the gene
     *
     * @param newRate the rate for polymerase binding
     */
    public void setPolymeraseAssociationRate( double newRate ) {
        _reactionProbabilities[2] = newRate;
    }

    /**
     * Sets the rate constant for RNA/ribosome association
     *
     * @param newRate the rate at which RNA binds to a ribosome
     */
    public void setRNARibosomeAssociationRate( double newRate ) {
        _reactionProbabilities[5] = newRate;
    }


    /**
     * Moves forward one time step in the simulation
     */
    public void takeStep() {
        double accumulatedTime = 0.0;
        double timeIncrement = -1.0;
        while ( accumulatedTime < _timeStep && timeIncrement != 0.0 ) {
            timeIncrement = simulateOneReaction( _timeStep - accumulatedTime );
            accumulatedTime += timeIncrement;
        }
        _currentTime += _timeStep;
    }

    /**
     * Moves forward one time step of specified length
     *
     * @param dt the length of this step through time
     */
    public void stepInTime( double dt ) {
        double accumulatedTime = 0.0;
        double timeIncrement = -1.0;
        while ( accumulatedTime < dt && timeIncrement != 0.0 ) {
            timeIncrement = simulateOneReaction( dt - accumulatedTime );
            accumulatedTime += timeIncrement;
        }
        _currentTime += dt;
    }

    /**
     * Simulates one reaction if the wait time before that reaction occurs is less than
     * maxTime
     *
     * @param maxTime the maximum of time to wait for this reaction to occur
     * @return the amount of time evolved in the system
     */
    public double simulateOneReaction( double maxTime ) {
        double[] a = calculateA();
        double a0 = sum( a );

        double r1 = _random.nextDouble();
        double r2 = _random.nextDouble();
        double tau = ( 1 / a0 ) * Math.log( 1 / r1 );
        if ( tau > maxTime ) { return 0.0; }

        int mu = 0;
        double sumSoFar = a[0];
        while ( sumSoFar < r2 * a0 ) {
            mu++;
            sumSoFar += a[mu];
        }
        conductReaction( mu );
        return tau;
    }

    private double sum( double[] toSum ) {
        double sum = 0.0;
        for ( int i = 0; i < toSum.length; i++ ) {
            sum += toSum[i];
        }
        return sum;
    }

    private double[] calculateA() {
        double[] h = new double[_reactionProbabilities.length];

        h[0] = _objectCounts[0] * _objectCounts[1];
        h[1] = _objectCounts[3];
        h[2] = _objectCounts[2] * _objectCounts[3];
        h[3] = _objectCounts[4];
        h[4] = _objectCounts[4];
        h[5] = _objectCounts[5] * _objectCounts[6];
        h[6] = _objectCounts[7];
        h[7] = _objectCounts[7];
        h[8] = _objectCounts[8];

        for ( int i = 0; i < h.length; i++ ) {
            h[i] *= _reactionProbabilities[i];
        }
        return h;
    }

    private void conductReaction( int mu ) {
        switch( mu ) {
            case 0:
                _objectCounts[0]--;
                _objectCounts[1]--;
                _objectCounts[3]++;
                break;
            case 1:
                _objectCounts[0]++;
                _objectCounts[1]++;
                _objectCounts[3]--;
                break;
            case 2:
                _objectCounts[3]--;
                _objectCounts[2]--;
                _objectCounts[4]++;
                break;
            case 3:
                _objectCounts[3]++;
                _objectCounts[2]++;
                _objectCounts[4]--;
                break;
            case 4:
                _objectCounts[0]++;
                _objectCounts[1]++;
                _objectCounts[2]++;
                _objectCounts[4]--;
                _objectCounts[5]++;
                break;
            case 5:
                _objectCounts[5]--;
                _objectCounts[6]--;
                _objectCounts[7]++;
                break;
            case 6:
                _objectCounts[5]++;
                _objectCounts[6]++;
                _objectCounts[7]--;
                break;
            case 7:
                _objectCounts[5]++;
                _objectCounts[6]++;
                _objectCounts[7]--;
                _objectCounts[8]++;
                break;
            case 8:
                _objectCounts[8]--;
        }
    }

    /**
     * Get the number of proteins currently in this cell.
     *
     * @return protein count
     */
    public int getProteinCount() {
        return _objectCounts[8];
    }

    /**
     * Sets the amount of time simulated in every step.
     *
     * @param timeStep new amount of time between simulation steps
     */
    public void setTimeStep( double timeStep ) {
        _timeStep = Math.abs( timeStep );
    }

    /**
     * Gets the amount of time simulated in every step.
     *
     * @return the amount of time between each simulation step
     */
    public double getTimeStep() {
        return _timeStep;
    }

    /**
     * Gets the amount of total simulation time
     *
     * @return total time
     */
    public double getTime() {
        return _currentTime;
    }
}
