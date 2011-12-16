// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.util.LinkedList;

/**
 * A population of synthetic cells.
 *
 * @Author George A. Emanuel
 */


public class CellPopulation {

    private LinkedList<CellFromGeorge> _cells = new LinkedList<CellFromGeorge>();
    private int _minRibosomeCount = 1000;
    private int _maxRibosomeCount = 2000;
    private double _timeStep = 5e2;
    private double _populationAge = 0.0;

    public CellPopulation( int initialCount ) {
        setPopulationCount( initialCount );
    }

    /**
     * Add or remove cells until the number of cells is at the specified level
     *
     * @param populationCount the number of desired cells in the population
     */
    public void setPopulationCount( int populationCount ) {
        if ( size() < populationCount ) {
            while ( size() < populationCount ) {
                addCell();
            }
        }
        else {
            while ( size() > populationCount ) {
                removeCell();
            }
        }
    }

    /**
     * Get the cells that are part of this population.
     *
     * @return a list of cells
     */
    public LinkedList<CellFromGeorge> getCells() {
        return _cells;
    }

    /**
     * Adds a cell to the this population.  A random ribosome count is selected.
     */
    public void addCell() {
        _cells.add( new CellFromGeorge( (int) ( Math.random() * ( _maxRibosomeCount - _minRibosomeCount ) +
                                                _minRibosomeCount ) ) );
    }

    /**
     * Removes a cell from this population
     */
    public void removeCell() {
        _cells.remove();
    }

    /**
     * Get the size of this population
     *
     * @return the number of cells
     */
    public int size() {
        return _cells.size();
    }


    /**
     * Advances all cells in this population through one time step
     */
    public void takePopulationStep() {
        for ( CellFromGeorge currentCell : _cells ) {
            currentCell.stepInTime( _timeStep );
        }
    }

    /**
     * Sets the number of transcription factors for all cells in this population
     *
     * @param tfCount number of transcription factors
     */
    public void setTranscriptionFactorCount( int tfCount ) {
        for ( CellFromGeorge currentCell : _cells ) {
            currentCell.setTranscriptionFactorCount( tfCount );
        }
    }

    /**
     * Sets the number of polymerases for all cells in this population
     *
     * @param polymeraseCount number of polymerases
     */
    public void setPolymeraseCount( int polymeraseCount ) {
        for ( CellFromGeorge currentCell : _cells ) {
            currentCell.setPolymeraseCount( polymeraseCount );
        }
    }

    /**
     * Sets the rate that transcription factors associate with genes for all
     * cells in this population
     *
     * @param newRate
     */
    public void setGeneTranscriptionFactorAssociationRate( double newRate ) {
        for ( CellFromGeorge currentCell : _cells ) {
            currentCell.setGeneTranscriptionFactorAssociationRate( newRate );
        }
    }

    /**
     * Sets the rate constant for the polymerase to bind to the gene for all cells
     * in this population
     *
     * @param newRate the rate for polymerase binding
     */
    public void setPolymeraseAssociationRate( double newRate ) {
        for ( CellFromGeorge currentCell : _cells ) {
            currentCell.setPolymeraseAssociationRate( newRate );
        }
    }

    /**
     * Sets the rate constant for RNA/ribosome association for all cells in
     * this population
     *
     * @param newRate the rate at which RNA binds to a ribosome
     */
    public void setRNARibosomeAssociationRate( double newRate ) {
        for ( CellFromGeorge currentCell : _cells ) {
            currentCell.setRNARibosomeAssociationRate( newRate );
        }
    }
}
