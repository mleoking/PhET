/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

/**
 * The gene for different colors of fur (singleton)
 *
 * @author Jonathan Olson
 */
public class ColorGene extends Gene {
    // fur alleles (also used for phenotypes)
    public static final Allele WHITE_ALLELE = new Allele( "White-Colored Fur" );
    public static final Allele BROWN_ALLELE = new Allele( "Brown-Colored Fur" );

    private ColorGene() {
        super( WHITE_ALLELE, BROWN_ALLELE );
    }

    private static ColorGene instance = null;

    /**
     * Get the main instance
     * @return The instance
     */
    public static ColorGene getInstance() {
        if ( instance == null ) {
            instance = new ColorGene();
        }
        return instance;
    }

    /**
     * Mutation probability
     * @return The fraction of bunnies that have a mutation when enabled.
     */
    public double getMutationFraction() {
        return 0.2;
    }

    /**
     * Extracts the correct genotype from a bunny
     * @param bunny The bunny you need the DNA from
     * @return The bunny's Genotype for this gene
     */
    public Genotype getBunnyGenotype( Bunny bunny ) {
        return bunny.getColorGenotype();
    }

    /**
     * The name of the gene
     * @return The name of the gene
     */
    public String getName() {
        return "Color";
    }
}
