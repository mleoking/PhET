/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

/**
 * The gene for different types of bunny teeth (singleton)
 *
 * @author Jonathan Olson
 */
public class TeethGene extends Gene {
    // teeth alleles (also used for phenotypes)
    public static final Allele TEETH_REGULAR_ALLELE = new Allele( "Regular Teeth" );
    public static final Allele TEETH_HUGE_ALLELE = new Allele( "Huge Teeth" );

    private TeethGene() {
        super( TEETH_REGULAR_ALLELE, TEETH_HUGE_ALLELE );
    }

    private static TeethGene instance = null;

    /**
     * Get the main instance
     *
     * @return The instance
     */
    public static TeethGene getInstance() {
        if ( instance == null ) {
            instance = new TeethGene();
        }
        return instance;
    }

    /**
     * Mutation probability
     *
     * @return The fraction of alleles that have a mutation when enabled.
     */
    public double getMutationFraction() {
        return 0.2;
    }

    /**
     * Extracts the correct genotype from a bunny
     *
     * @param bunny The bunny you need the DNA from
     * @return The bunny's Genotype for this gene
     */
    public Genotype getBunnyGenotype( Bunny bunny ) {
        return bunny.getTeethGenotype();
    }

    /**
     * The name of the gene
     *
     * @return The name of the gene
     */
    public String getName() {
        return "Teeth";
    }
}