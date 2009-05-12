package edu.colorado.phet.acidbasesolutions.model;


public abstract class ConcentrationModel {
    
    private final Solute solute;
    
    protected ConcentrationModel( Solute solute ) {
        this.solute = solute;
    }
    
    public Solute getSolute() {
        return solute;
    }
    
    // c
    public double getInitialConcentration() {
        return solute.getInitialConcentration();
    }
    
    public abstract double getH3OConcentration();
    
    public abstract double getOHConcentration();
    
    public abstract double getH2OConcentration();
}
