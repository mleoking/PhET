package edu.colorado.phet.naturalselection.model;

public class ColorGene extends Gene {

    public static final Allele WHITE_ALLELE = new Allele( "White-Colored Fur" );
    public static final Allele BROWN_ALLELE = new Allele( "Brown-Colored Fur" );

    private ColorGene() {
        super( WHITE_ALLELE, BROWN_ALLELE );
    }

    private static ColorGene instance = null;

    public static ColorGene getInstance() {
        if ( instance == null ) {
            instance = new ColorGene();
        }
        return instance;
    }

    public double getMutationFraction() {
        return 0.2;
    }

    public Genotype getBunnyGenotype( Bunny bunny ) {
        return bunny.getColorGenotype();
    }

    public String getName() {
        return "Color";
    }
}
