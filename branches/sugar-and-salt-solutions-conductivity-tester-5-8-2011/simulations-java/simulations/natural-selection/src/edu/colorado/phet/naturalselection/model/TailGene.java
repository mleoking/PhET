// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.model;

import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;

/**
 * The gene for different types (lengths?) of bunny tails. Singleton
 *
 * @author Jonathan Olson
 */
public class TailGene extends Gene {
    // tail alleles (also used for phenotypes)
    public static final Allele TAIL_SHORT_ALLELE = new Allele( NaturalSelectionStrings.GENE_TAIL_SHORT );
    public static final Allele TAIL_LONG_ALLELE = new Allele( NaturalSelectionStrings.GENE_TAIL_LONG );

    private TailGene() {
        super( TAIL_SHORT_ALLELE, TAIL_LONG_ALLELE, NaturalSelectionDefaults.TAIL_DOMINANT_ALLELE );
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
     * Extracts the correct genotype from a bunny
     *
     * @param bunny The bunny you need the DNA from
     * @return The bunny's Genotype for this gene
     */
    public Genotype getBunnyGenotype( Bunny bunny ) {
        return bunny.getTailGenotype();
    }

    public Allele getBunnyPhenotype( Bunny bunny ) {
        return bunny.getTailPhenotype();
    }

    /**
     * The name of the gene
     *
     * @return The name of the gene
     */
    public String getName() {
        return NaturalSelectionStrings.GENE_TAIL_NAME;
    }
}