/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.TraitControlNode;

/**
 * Represents a Gene using Mendelian genetics that contains only two traits.
 * One of the traits is considered to be the primary one, and the other the secondary.
 * <p/>
 * The dominant trait can be changed
 *
 * @author Jonathan Olson
 */
public abstract class Gene implements Bunny.BunnyListener, TraitControlNode.TraitControlNodeListener {

    /**
     * The primary allele (usually the default)
     */
    private Allele primaryAllele;

    /**
     * The secondary allele (usually the mutation)
     */
    private Allele secondaryAllele;

    // distribution counts of the various traits
    private int primaryCount = 0;
    private int secondaryCount = 0;

    /**
     * The dominant allele
     */
    private Allele dominantAllele;

    /**
     * Whether or not this gene will mutate
     */
    private boolean mutatable;

    private NaturalSelectionModel model;
    private ArrayList listeners;

    /**
     * Constructor, builds a gene from two alleles
     *
     * @param primaryAllele   The primary (usually default) allele
     * @param secondaryAllele The secondary (usually mutated) allele
     */
    protected Gene( Allele primaryAllele, Allele secondaryAllele ) {

        this.primaryAllele = primaryAllele;
        this.secondaryAllele = secondaryAllele;

        dominantAllele = this.primaryAllele;

        listeners = new ArrayList();

    }

    public void reset() {
        dominantAllele = primaryAllele;
        primaryCount = secondaryCount = 0;
        mutatable = false;
    }

    /**
     * Sets the natural selection model, since genes are constructed as singletons
     *
     * @param model The natural selection model they apply to
     */
    public void setModel( NaturalSelectionModel model ) {
        this.model = model;
    }

    //----------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------

    public Allele getPrimaryAllele() {
        return primaryAllele;
    }

    public Allele getSecondaryAllele() {
        return secondaryAllele;
    }

    public Allele getDominantAllele() {
        return dominantAllele;
    }

    public void setDominantAllele( Allele _dominantAllele ) {
        if ( dominantAllele == _dominantAllele ) {
            return;
        }

        if ( _dominantAllele != primaryAllele && _dominantAllele != secondaryAllele ) {
            System.out.println( "WARNING: Attempting to set a dominant allele that is unrelated." );
            return;
        }

        dominantAllele = _dominantAllele;

        // dominant allele changed, notify everything
        notifyChangeDominantAllele();

        // refresh the distribution counts
        refreshPhenotypeCount();
    }

    public int getPrimaryPhenotypeCount() {
        return primaryCount;
    }

    public int getSecondaryPhenotypeCount() {
        return secondaryCount;
    }

    public int getPhenotypeCount( Allele allele ) {
        if ( allele == primaryAllele ) {
            return getPrimaryPhenotypeCount();
        }
        else if ( allele == secondaryAllele ) {
            return getSecondaryPhenotypeCount();
        }
        else {
            throw new RuntimeException( "Cannot get a count for unrelated allele" );
        }
    }

    /**
     * Recount the number of alive bunnies with the primary and secondary alleles
     * If it changed, then notify listeners
     */
    public void refreshPhenotypeCount() {
        int oldPrimary = primaryCount;
        int oldSecondary = secondaryCount;

        primaryCount = 0;
        secondaryCount = 0;

        // only count alive bunnies
        ArrayList bunnies = model.getAliveBunnyList();
        Iterator iter = bunnies.iterator();
        while ( iter.hasNext() ) {
            Bunny bunny = (Bunny) iter.next();
            Allele allele = getBunnyGenotype( bunny ).getPhenotype();
            if ( allele == primaryAllele ) {
                primaryCount++;
            }
            else if ( allele == secondaryAllele ) {
                secondaryCount++;
            }
            else {
                throw new RuntimeException( "Cannot get a count for unrelated allele" );
            }

            bunny.checkPhenotypes();
        }

        System.out.println( "\tDistribution for Gene " + getName() + ": " + primaryCount + ", " + secondaryCount );

        if ( oldPrimary != primaryCount || oldSecondary != secondaryCount ) {
            System.out.println( "\tDistribution changed!" );
            notifyChangeDistribution();
        }
    }


    public boolean getMutatable() {
        return mutatable;
    }

    public void setMutatable( boolean maybe ) {
        System.out.println( "Gene " + getName() + " will now start mutating" );
        mutatable = maybe;
    }

    /**
     * Subclasses should implement this to return the fraction of alleles that will mutate when passed on.
     *
     * @return
     */
    public abstract double getMutationFraction();

    /**
     * Return a (possibly) mutated allele
     *
     * @param base The starting allele
     * @return A possibly mutated allele
     */
    public Allele mutatedAllele( Allele base ) {
        if ( !getMutatable() ) {
            return base;
        }

        if ( Math.random() < getMutationFraction() ) {
            if ( base == primaryAllele ) {
                return secondaryAllele;
            }
            else {
                return primaryAllele;
            }
        }

        return base;
    }

    /**
     * The name of the gene
     *
     * @return The name of the gene
     */
    public abstract String getName();

    /**
     * Should return the genotype of the particular bunny.
     *
     * @param bunny The bunny
     * @return The genotype of the bunny for this particular gene
     */
    public abstract Genotype getBunnyGenotype( Bunny bunny );

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onBunnyInit( Bunny bunny ) {
        // not in bunnies array yet, don't refresh the phenotype count
        // here we manually increment the counts

        if ( getBunnyGenotype( bunny ).getPhenotype() == primaryAllele ) {
            primaryCount++;
        }
        else {
            secondaryCount++;
        }

        notifyChangeDistribution();
    }

    public void onBunnyDeath( Bunny bunny ) {
        refreshPhenotypeCount();
    }

    public void onBunnyReproduces( Bunny bunny ) {

    }

    public void onBunnyAging( Bunny bunny ) {

    }

    public void onBunnyChangeColor( Allele allele ) {

    }

    public void onBunnyChangeTeeth( Allele allele ) {

    }

    public void onBunnyChangeTail( Allele allele ) {

    }

    public void onChangeDominance( boolean primary ) {
        if ( primary ) {
            setDominantAllele( primaryAllele );
        }
        else {
            setDominantAllele( secondaryAllele );
        }
    }

    public void onAddMutation() {
        setMutatable( true );
    }

    //----------------------------------------------------------------------------
    // Notifiers
    //----------------------------------------------------------------------------

    private void notifyChangeDistribution() {
        System.out.println( "\tGene distribution changed for " + getName() );
        Iterator iter = listeners.iterator();

        while ( iter.hasNext() ) {
            ( (GeneListener) iter.next() ).onChangeDistribution( getPrimaryPhenotypeCount(), getSecondaryPhenotypeCount() );
        }
    }

    private void notifyChangeDominantAllele() {
        System.out.println( "Gene dominant allele changed for " + getName() );
        Iterator iter = listeners.iterator();

        while ( iter.hasNext() ) {
            ( (GeneListener) iter.next() ).onChangeDominantAllele( primaryAllele == dominantAllele );
        }
    }

    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addListener( GeneListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( GeneListener listener ) {
        listeners.remove( listener );
    }

}
