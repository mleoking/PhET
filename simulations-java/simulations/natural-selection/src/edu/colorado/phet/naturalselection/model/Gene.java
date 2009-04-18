package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public abstract class Gene implements Bunny.BunnyListener {
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
        }

        if ( oldPrimary != primaryCount || oldSecondary != secondaryCount ) {
            notifyChangeDistribution();
        }
    }


    public boolean getMutatable() {
        return mutatable;
    }

    public void setMutatable( boolean maybe ) {
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


    public abstract Genotype getBunnyGenotype( Bunny bunny );

    public void onBunnyInit( Bunny bunny ) {
        refreshPhenotypeCount();
    }

    public void onBunnyDeath( Bunny bunny ) {
        refreshPhenotypeCount();
    }

    public void onBunnyReproduces( Bunny bunny ) {

    }

    public void onBunnyAging( Bunny bunny ) {

    }


    // notifiers

    private void notifyChangeDistribution() {
        Iterator iter = listeners.iterator();

        while ( iter.hasNext() ) {
            ( (GeneListener) iter.next() ).onChangeDistribution();
        }
    }

    private void notifyChangeDominantAllele() {
        Iterator iter = listeners.iterator();

        while ( iter.hasNext() ) {
            ( (GeneListener) iter.next() ).onChangeDominantAllele();
        }
    }

    // listeners

    public void addListener( GeneListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( GeneListener listener ) {
        listeners.remove( listener );
    }

    public interface GeneListener {
        public void onChangeDominantAllele();

        public void onChangeDistribution();
    }

}
