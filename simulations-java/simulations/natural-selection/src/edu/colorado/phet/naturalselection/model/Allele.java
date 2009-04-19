/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

/**
 * Allele is used to store both alleles and phenotypes (since we are using Mendelian genetics)
 *
 * @author Jonathan Olson
 */
public class Allele {

    private String name;

    /**
     * Allele constructor
     *
     * @param _name A display name for the allele or the description of its corresponding trait
     */
    public Allele( String _name ) {
        name = _name;
    }

    /**
     * Gets the name of the allele
     *
     * @return The name of the Allele
     */
    public String getName() {
        return name;
    }

    /**
     * Get the single-character representation
     *
     * @return A single-letter representation of the Allele
     */
    public String toString() {
        String ret;
        if ( this == ColorGene.WHITE_ALLELE ) {
            return "w";
        }
        else if ( this == ColorGene.BROWN_ALLELE ) {
            return "b";
        }
        else if ( this == TeethGene.TEETH_REGULAR_ALLELE ) {
            return "r";
        }
        else if ( this == TeethGene.TEETH_HUGE_ALLELE ) {
            return "g";
        }
        else if ( this == TailGene.TAIL_SHORT_ALLELE ) {
            return "s";
        }
        else if ( this == TailGene.TAIL_LONG_ALLELE ) {
            return "l";
        }

        return "?";

    }

}
