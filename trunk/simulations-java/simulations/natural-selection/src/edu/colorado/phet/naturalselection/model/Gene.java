package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.TraitControlNode;

public abstract class Gene implements Bunny.BunnyListener, TraitControlNode.TraitControlNodeListener {
    private ArrayList listeners;

    private Allele primaryAllele;
    private Allele secondaryAllele;

    private int primaryCount = 0;
    private int secondaryCount = 0;

    private Allele dominantAllele;
    private boolean mutatable;

    private NaturalSelectionModel model;

    protected Gene( Allele _primaryAllele, Allele _secondaryAllele ) {

        primaryAllele = _primaryAllele;
        secondaryAllele = _secondaryAllele;

        dominantAllele = primaryAllele;

        listeners = new ArrayList();

    }

    public void reset() {
        dominantAllele = primaryAllele;
        primaryCount = secondaryCount = 0;
        mutatable = false;
    }

    public void setModel( NaturalSelectionModel _model ) {
        model = _model;
    }


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

        notifyChangeDominantAllele();

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

    public void refreshPhenotypeCount() {
        int oldPrimary = primaryCount;
        int oldSecondary = secondaryCount;

        primaryCount = 0;
        secondaryCount = 0;

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

    public abstract double getMutationFraction();

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

    public abstract String getName();

    public abstract Genotype getBunnyGenotype( Bunny bunny );

    public void onBunnyInit( Bunny bunny ) {
        // not in bunnies array yet
        //refreshPhenotypeCount();
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

    // notifiers

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

    // listeners

    public void addListener( GeneListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( GeneListener listener ) {
        listeners.remove( listener );
    }

}
