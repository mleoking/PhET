package edu.colorado.phet.naturalselection.model;

/**
 * The interface for objects that want to receive events from Genes
 *
 * @author Jonathan Olson
 */
public interface GeneListener {
    /**
     * Event sent when the dominant allele is changed
     *
     * @param gene    The affected gene
     * @param primary Whether the new dominant allele is the primary or secondary allele
     */
    public void onChangeDominantAllele( Gene gene, boolean primary ); // TODO: change to the allele, AND send primary?

    /**
     * Event sent when the distribution of traits in bunnies changes
     *
     * @param gene      The affected gene
     * @param primary   Number of bunnies with the primary phenotype
     * @param secondary Number of bunnies with the second phenotype
     */
    public void onChangeDistribution( Gene gene, int primary, int secondary );

    /**
     * Event sent when the mutatability of the gene changes
     *
     * @param gene      The affected gene
     * @param mutatable Whether the gene will mutate or not
     */
    public void onChangeMutatable( Gene gene, boolean mutatable );
}