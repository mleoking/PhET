package edu.colorado.phet.naturalselection.model;

public interface GeneListener {
    public void onChangeDominantAllele( boolean primary );

    public void onChangeDistribution( int primary, int secondary );
}