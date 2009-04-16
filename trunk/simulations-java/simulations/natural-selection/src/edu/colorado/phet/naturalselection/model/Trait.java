package edu.colorado.phet.naturalselection.model;

import java.util.ArrayList;

public class Trait implements Bunny.BunnyListener {

    public static final int TRAIT_COLOR_WHITE = 0;
    public static final int TRAIT_COLOR_BROWN = 1;

    public static final int TRAIT_EARS_SHORT = 2;
    public static final int TRAIT_EARS_LONG = 3;
    

    private ArrayList listeners;


    private int primaryAllele;
    private int secondaryAllele;

    private int primaryCount = 0;
    private int secondaryCount = 0;

    private int dominantAllele;

    public Trait( int _primaryAllele, int _secondaryAllele ) {

        primaryAllele = _primaryAllele;
        secondaryAllele = _secondaryAllele;

        dominantAllele = primaryAllele;

        listeners = new ArrayList();

    }

    public int getPrimaryAllele() {
        return primaryAllele;
    }

    public int getSecondaryAllele() {
        return secondaryAllele;
    }

    public int getDominantAllele() {
        return dominantAllele;
    }

    public void setDominantAllele( int _dominantAllele ) {
        if( _dominantAllele != primaryAllele && _dominantAllele != secondaryAllele ) {
            System.out.println( "WARNING: Attempting to set a dominant allele that is unrelated." );
            return;
        }

        dominantAllele = _dominantAllele;
    }


    public void onBunnyInit( Bunny bunny ) {
        
    }

    public void onBunnyDeath( Bunny bunny ) {

    }

    public void onBunnyReproduces( Bunny bunny ) {

    }

    public void onBunnyAging( Bunny bunny ) {
        
    }



    // listeners

    public void addListener( TraitListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( TraitListener listener ) {
        listeners.remove( listener );
    }

    public interface TraitListener {
        public void onChangeDistribution();
    }

}
