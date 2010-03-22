/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.persistence;


/**
 * ComparingConfig is a Java Bean compliant configuration of ComparingModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingConfig extends AbstractModuleConfig {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // left solution
    private String soluteNameLeft;
    private double concentrationLeft;
    private double strengthLeft;
    
    // right solution
    private String soluteNameRight;
    private double concentrationRight;
    private double strengthRight;
    
    // view controls
    private boolean beakersSelected;
    private boolean graphsSelected;
    private boolean equationsSelected;
    
    // beaker view controls
    private boolean soluteComponentsRatioVisible;
    private boolean hydroniumHydroxideRatioVisible;
    private boolean moleculeCountsVisible;
    private boolean beakerLabelVisible;
    
    // equations controls
    private boolean equationsScalingEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public ComparingConfig() {}

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public String getSoluteNameLeft() {
        return soluteNameLeft;
    }

    
    public void setSoluteNameLeft( String soluteNameLeft ) {
        this.soluteNameLeft = soluteNameLeft;
    }

    
    public double getConcentrationLeft() {
        return concentrationLeft;
    }

    
    public void setConcentrationLeft( double concentrationLeft ) {
        this.concentrationLeft = concentrationLeft;
    }

    
    public double getStrengthLeft() {
        return strengthLeft;
    }

    
    public void setStrengthLeft( double strengthLeft ) {
        this.strengthLeft = strengthLeft;
    }

    
    public String getSoluteNameRight() {
        return soluteNameRight;
    }

    
    public void setSoluteNameRight( String soluteNameRight ) {
        this.soluteNameRight = soluteNameRight;
    }

    
    public double getConcentrationRight() {
        return concentrationRight;
    }

    
    public void setConcentrationRight( double concentrationRight ) {
        this.concentrationRight = concentrationRight;
    }

    
    public double getStrengthRight() {
        return strengthRight;
    }

    
    public void setStrengthRight( double strengthRight ) {
        this.strengthRight = strengthRight;
    }

    
    public boolean isBeakersSelected() {
        return beakersSelected;
    }

    
    public void setBeakersSelected( boolean beakersSelected ) {
        this.beakersSelected = beakersSelected;
    }

    
    public boolean isGraphsSelected() {
        return graphsSelected;
    }

    
    public void setGraphsSelected( boolean graphsSelected ) {
        this.graphsSelected = graphsSelected;
    }

    
    public boolean isEquationsSelected() {
        return equationsSelected;
    }

    
    public void setEquationsSelected( boolean equationsSelected ) {
        this.equationsSelected = equationsSelected;
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

    
    public boolean isEquationsScalingEnabled() {
        return equationsScalingEnabled;
    }

    
    public void setEquationsScalingEnabled( boolean equationsSalingEnabled ) {
        this.equationsScalingEnabled = equationsSalingEnabled;
    }

}
