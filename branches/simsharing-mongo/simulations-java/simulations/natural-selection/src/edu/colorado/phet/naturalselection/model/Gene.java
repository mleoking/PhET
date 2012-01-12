// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Gene using Mendelian genetics that contains only two traits.
 * One of the traits is considered to be the primary one, and the other the secondary.
 * <p/>
 * The dominant trait can be changed
 *
 * @author Jonathan Olson
 */
public abstract class Gene implements Bunny.Listener {

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

    private Allele defaultDominantAllele;

    /**
     * Whether or not this gene will mutate (during the next generation)
     */
    private boolean mutatable;

    private NaturalSelectionModel model;
    private List<GeneListener> listeners;

    /**
     * Constructor, builds a gene from two alleles
     *
     * @param primaryAllele         The primary (usually default) allele
     * @param secondaryAllele       The secondary (usually mutated) allele
     * @param defaultDominantAllele The default dominant allele
     */
    protected Gene( Allele primaryAllele, Allele secondaryAllele, Allele defaultDominantAllele ) {

        this.primaryAllele = primaryAllele;
        this.secondaryAllele = secondaryAllele;

        this.defaultDominantAllele = defaultDominantAllele;

        dominantAllele = defaultDominantAllele;

        listeners = new LinkedList<GeneListener>();

    }

    public void reset() {
        dominantAllele = defaultDominantAllele;
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
        if ( model == null ) {
            return;
        }

        int oldPrimary = primaryCount;
        int oldSecondary = secondaryCount;

        primaryCount = 0;
        secondaryCount = 0;

        // only count alive bunnies
        List<Bunny> bunnies = model.getAliveBunnyList();
        for ( Bunny bunny : bunnies ) {
            Allele allele = getBunnyPhenotype( bunny );
            if ( allele == primaryAllele ) {
                primaryCount++;
            }
            else if ( allele == secondaryAllele ) {
                secondaryCount++;
            }
            else {
                throw new RuntimeException( "Cannot get a count for unrelated allele" );
            }
        }

        if ( oldPrimary != primaryCount || oldSecondary != secondaryCount ) {
            notifyChangeDistribution();
        }
    }

    /**
     * Whether or not this gene is set to mutate
     *
     * @return Whether this gene will mutate during the next generation
     */
    public boolean getMutatable() {
        return mutatable;
    }

    /**
     * Set whether this gene will mutate for the next generation
     *
     * @param mutate Whether or not to mutate
     */
    public void setMutatable( boolean mutate ) {
        boolean old = mutatable;
        mutatable = mutate;
        if ( old != mutatable ) {
            notifyChangeMutatable();
        }
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

    public abstract Allele getBunnyPhenotype( Bunny bunny );

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onChangeDominance( boolean primary ) {
        if ( primary ) {
            setDominantAllele( primaryAllele );
        }
        else {
            setDominantAllele( secondaryAllele );
        }
    }

    public void onEvent( Bunny.Event event ) {
        Bunny bunny = event.getBunny();
        switch( event.type ) {
            case Bunny.Event.TYPE_INIT:
                // not in bunnies array yet, don't refresh the phenotype count
                // here we manually increment the counts

                if ( getBunnyPhenotype( bunny ) == primaryAllele ) {
                    primaryCount++;
                }
                else {
                    secondaryCount++;
                }

                notifyChangeDistribution();
                break;
            case Bunny.Event.TYPE_DIED:
                refreshPhenotypeCount();
                break;
        }
    }

    //----------------------------------------------------------------------------
    // Notifiers
    //----------------------------------------------------------------------------

    private void notifyChangeDistribution() {
        Iterator<GeneListener> iter = listeners.iterator();

        while ( iter.hasNext() ) {
            ( iter.next() ).onChangeDistribution( this, getPrimaryPhenotypeCount(), getSecondaryPhenotypeCount() );
        }
    }

    private void notifyChangeDominantAllele() {
        Iterator<GeneListener> iter = listeners.iterator();

        while ( iter.hasNext() ) {
            ( iter.next() ).onChangeDominantAllele( this, primaryAllele == dominantAllele );
        }
    }

    private void notifyChangeMutatable() {
        Iterator<GeneListener> iter = listeners.iterator();

        while ( iter.hasNext() ) {
            ( iter.next() ).onChangeMutatable( this, getMutatable() );
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
