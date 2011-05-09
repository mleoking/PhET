// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.model;

import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;

/**
 * The gene for different types of bunny teeth (singleton)
 *
 * @author Jonathan Olson
 */
public class TeethGene extends Gene {
    // teeth alleles (also used for phenotypes)
    public static final Allele TEETH_SHORT_ALLELE = new Allele( NaturalSelectionStrings.GENE_TEETH_SHORT );
    public static final Allele TEETH_LONG_ALLELE = new Allele( NaturalSelectionStrings.GENE_TEETH_LONG );

    private TeethGene() {
        super( TEETH_SHORT_ALLELE, TEETH_LONG_ALLELE, NaturalSelectionDefaults.TEETH_DOMINANT_ALLELE );
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
     * Extracts the correct genotype from a bunny
     *
     * @param bunny The bunny you need the DNA from
     * @return The bunny's Genotype for this gene
     */
    public Genotype getBunnyGenotype( Bunny bunny ) {
        return bunny.getTeethGenotype();
    }

    public Allele getBunnyPhenotype( Bunny bunny ) {
        return bunny.getTeethPhenotype();
    }

    /**
     * The name of the gene
     *
     * @return The name of the gene
     */
    public String getName() {
        return NaturalSelectionStrings.GENE_TEETH_NAME;
    }
}