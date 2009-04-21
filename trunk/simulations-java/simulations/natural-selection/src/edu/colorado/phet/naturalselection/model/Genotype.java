/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

/**
 * Stores an individual bunny's DNA for a particular gene for simple Mendelian genetics.
 * IE: Only stores two alleles, one from the father and one from the mother
 *
 * @author Jonathan Olson
 */
public class Genotype {

    /**
     * The gene that this genotype is for
     */
    private Gene gene;

    /**
     * The allele this individual received from their father
     */
    private Allele fatherAllele;

    /**
     * The allele this individual received from their mother
     */
    private Allele motherAllele;

    /**
     * Constructor
     *
     * @param gene         The gene
     * @param fatherAllele The allele on the father's side
     * @param motherAllele The allele on the mother's side
     */
    public Genotype( Gene gene, Allele fatherAllele, Allele motherAllele ) {
        this.gene = gene;
        this.fatherAllele = fatherAllele;
        this.motherAllele = motherAllele;
    }

    /**
     * Gets the phenotype (visible trait) from the combination of alleles. In this simple case, if the bunny has
     * one or more of the dominant alleles, it displays the dominant trait. Otherwise, it displays the recessive trait.
     *
     * @return The phenotype
     */
    public Allele getPhenotype() {
        if ( fatherAllele == motherAllele ) {
            return fatherAllele;
        }
        return gene.getDominantAllele();
    }

    /**
     * Returns one of the (possibly mutated) alleles
     *
     * @return One of the bunny's possibly mutated alleles
     */
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
