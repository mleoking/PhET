package edu.colorado.phet.naturalselection.model;

public class Allele {

    private String name;

    public Allele( String _name ) {
        name = _name;
    }

    public String getName() {
        return name;
    }

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
