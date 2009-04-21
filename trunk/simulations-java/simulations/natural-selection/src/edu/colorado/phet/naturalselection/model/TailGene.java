/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

public class TailGene extends Gene {

    public static final Allele TAIL_SHORT_ALLELE = new Allele( "Short Tail" );
    public static final Allele TAIL_LONG_ALLELE = new Allele( "Long Tail" );

    private TailGene() {
        super( TAIL_SHORT_ALLELE, TAIL_LONG_ALLELE );
    }

    private static TailGene instance = null;

    public static TailGene getInstance() {
        if ( instance == null ) {
            instance = new TailGene();
        }
        return instance;
    }

    public double getMutationFraction() {
        return 0.2;
    }

    public Genotype getBunnyGenotype( Bunny bunny ) {
        return bunny.getTailGenotype();
    }

    public String getName() {
        return "Tail";
    }
}