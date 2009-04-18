package edu.colorado.phet.naturalselection.model;

public class TeethGene extends Gene {

    public static final Allele TEETH_REGULAR_ALLELE = new Allele( "Regular Teeth" );
    public static final Allele TEETH_HUGE_ALLELE = new Allele( "Huge Teeth" );

    private TeethGene() {
        super( TEETH_REGULAR_ALLELE, TEETH_HUGE_ALLELE );
    }

    private static TeethGene instance = null;

    public static TeethGene getInstance() {
        if ( instance == null ) {
            instance = new TeethGene();
        }
        return instance;
    }

    public double getMutationFraction() {
        return 0.2;
    }

    public Genotype getBunnyGenotype( Bunny bunny ) {
        return bunny.getTeethGenotype();
    }

    public String getName() {
        return "Teeth";
    }
}