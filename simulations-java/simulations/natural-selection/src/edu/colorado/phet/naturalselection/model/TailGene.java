/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

/**
 * The gene for different types (lengths?) of bunny tails. Singleton
 *
 * @author Jonathan Olson
 */
public class TailGene extends Gene {
    // tail alleles (also used for phenotypes)
    public static final Allele TAIL_SHORT_ALLELE = new Allele( "Short Tail" );
    public static final Allele TAIL_LONG_ALLELE = new Allele( "Long Tail" );

    private TailGene() {
        super( TAIL_SHORT_ALLELE, TAIL_LONG_ALLELE );
    }

    private static TailGene instance = null;

    /**
     * Get the main instance
     *
     * @return The instance
     */
    public static TailGene getInstance() {
        if ( instance == null ) {
            instance = new TailGene();
        }
        return instance;
    }

    /**
     * Mutation probability
     *
     * @return The fraction of alleles that have a mutation when enabled.
     */
    public double getMutationFraction() {
        return 0.07;
    }

    /**
     * Extracts the correct genotype from a bunny
     *
     * @param bunny The bunny you need the DNA from
     * @return The bunny's Genotype for this gene
     */
    public Genotype getBunnyGenotype( Bunny bunny ) {
        return bunny.getTailGenotype();
    }

    /**
     * The name of the gene
     *
     * @return The name of the gene
     */
    public String getName() {
        return "Tail";
    }
}