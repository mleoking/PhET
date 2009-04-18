package edu.colorado.phet.naturalselection.model;

public class Genotype {

    private Gene gene;
    private Allele fatherAllele;
    private Allele motherAllele;

    public Genotype( Gene _gene, Allele _fatherAllele, Allele _motherAllele ) {
        gene = _gene;
        fatherAllele = _fatherAllele;
        motherAllele = _motherAllele;
    }

    public Allele getPhenotype() {
        if ( fatherAllele == motherAllele ) {
            return fatherAllele;
        }
        return gene.getDominantAllele();
    }

    public Allele getRandomAllele() {
        if ( Math.random() < 0.5 ) {
            return gene.mutatedAllele( fatherAllele );
        }
        else {
            return gene.mutatedAllele( motherAllele );
        }
    }

    public Gene getGene() {
        return gene;
    }

    public Allele getFatherAllele() {
        return fatherAllele;
    }

    public Allele getMotherAllele() {
        return motherAllele;
    }

    public String toString() {
        return fatherAllele.toString() + motherAllele.toString();
    }

}
