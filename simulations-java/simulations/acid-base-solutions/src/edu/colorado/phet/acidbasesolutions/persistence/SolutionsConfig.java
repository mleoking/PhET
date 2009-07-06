/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.persistence;


/**
 * SolutionsConfig is a Java Bean compliant configuration of SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsConfig extends AbstractModuleConfig {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // solution
    private String soluteName;
    private double concentration;
    private double strength;
    
    // beaker view controls
    private boolean soluteComponentsRatioVisible;
    private boolean hydroniumHydroxideRatioVisible;
    private boolean moleculeCountsVisible;
    private boolean beakerLabelVisible;
    
    // misc view controls
    private boolean concentrationGraphVisible;
    private boolean symbolLegendVisible;
    private boolean equilibriumExpressionsVisible;
    private boolean reactionEquationsVisible;
    
    // dialogs
    private boolean equilibriumExpressionsScalingEnabled;
    private boolean reactionEquationsScalingEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public SolutionsConfig() {}

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public String getSoluteName() {
        return soluteName;
    }

    
    public void setSoluteName( String soluteName ) {
        this.soluteName = soluteName;
    }

    
    public double getConcentration() {
        return concentration;
    }

    
    public void setConcentration( double concentration ) {
        this.concentration = concentration;
    }

    
    public double getStrength() {
        return strength;
    }

    
    public void setStrength( double strength ) {
        this.strength = strength;
    }

    
    public boolean isSoluteComponentsRatioVisible() {
        return soluteComponentsRatioVisible;
    }

    
    public void setSoluteComponentsRatioVisible( boolean visible ) {
        this.soluteComponentsRatioVisible = visible;
    }

    
    public boolean isHydroniumHydroxideRatioVisible() {
        return hydroniumHydroxideRatioVisible;
    }

    
    public void setHydroniumHydroxideRatioVisible( boolean visible ) {
        this.hydroniumHydroxideRatioVisible = visible;
    }

    
    public boolean isMoleculeCountsVisible() {
        return moleculeCountsVisible;
    }

    
    public void setMoleculeCountsVisible( boolean moleculeCountsVisible ) {
        this.moleculeCountsVisible = moleculeCountsVisible;
    }

    
    public boolean isBeakerLabelVisible() {
        return beakerLabelVisible;
    }

    
    public void setBeakerLabelVisible( boolean beakerLabelVisible ) {
        this.beakerLabelVisible = beakerLabelVisible;
    }

    
    public boolean isConcentrationGraphVisible() {
        return concentrationGraphVisible;
    }

    
    public void setConcentrationGraphVisible( boolean concentrationGraphVisible ) {
        this.concentrationGraphVisible = concentrationGraphVisible;
    }

    
    public boolean isSymbolLegendVisible() {
        return symbolLegendVisible;
    }

    
    public void setSymbolLegendVisible( boolean symbolLegendVisible ) {
        this.symbolLegendVisible = symbolLegendVisible;
    }

    
    public boolean isEquilibriumExpressionsVisible() {
        return equilibriumExpressionsVisible;
    }

    
    public void setEquilibriumExpressionsVisible( boolean equilibriumExpressionsVisible ) {
        this.equilibriumExpressionsVisible = equilibriumExpressionsVisible;
    }

    
    public boolean isReactionEquationsVisible() {
        return reactionEquationsVisible;
    }

    
    public void setReactionEquationsVisible( boolean reactionEquationsVisible ) {
        this.reactionEquationsVisible = reactionEquationsVisible;
    }
    
    public boolean isEquilibriumExpressionsScalingEnabled() {
        return equilibriumExpressionsScalingEnabled;
    }

    
    public void setEquilibriumExpressionsScalingEnabled( boolean equilibriumExpressionsScalingEnabled ) {
        this.equilibriumExpressionsScalingEnabled = equilibriumExpressionsScalingEnabled;
    }

    
    public boolean isReactionEquationsScalingEnabled() {
        return reactionEquationsScalingEnabled;
    }

    
    public void setReactionEquationsScalingEnabled( boolean reactionEquationsScalingEnabled ) {
        this.reactionEquationsScalingEnabled = reactionEquationsScalingEnabled;
    }
}
