/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

/**
 * The interface for objects that want to receive events from Genes
 *
 * @author Jonathan Olson
 */
public interface GeneListener {
    /**
     * Event sent when the dominant allele is changed
     * @param primary Whether the new dominant allele is the primary or secondary allele
     */
    public void onChangeDominantAllele( boolean primary ); // TODO: change to the allele, AND send primary?

    /**
     * Event sent when the distribution of traits in bunnies changes
     * @param primary Number of bunnies with the primary phenotype
     * @param secondary Number of bunnies with the second phenotype
     */
    public void onChangeDistribution( int primary, int secondary );
}