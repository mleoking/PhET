/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.common.phetcommon.math.MathUtil;

/** 
 * In chemistry, a solution is a homogeneous mixture composed of two or more
 * substances. In such a mixture, a solute is dissolved in another substance,
 * known as a solvent. In an aqueous solution, the solvent is water.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AqueousSolution {
    
    private static final double W = 55.6; // H2O concentration, mol/L
    private static final double Kw = 1E-14; // H2O equilibrium constant

    private final Solute solute;
    private double initialConcentration; // initial concentration of the solute, at start of reaction
    private final EventListenerList listeners;
    
    public AqueousSolution( Solute solute, double initialConcentration ) {
        this.solute = solute;
        this.initialConcentration = initialConcentration;
        listeners = new EventListenerList();
    }
    
    public Color getColor() {
        return ABSColors.AQUEOUS_SOLUTION;
    }

    public Solute getSolute() {
        return solute;
    }
    
    /**
     * Sets the initial concentration of the solute, at the start of the reaction.
     * @param initialConcentration
     */
    public void setInitialConcentration( double initialConcentration ) {
        if ( initialConcentration != this.initialConcentration ) {
            this.initialConcentration = initialConcentration;
            fireInitialConcentrationChanged();
        }
    }
    
    /**
     * Gets the initial concentration of the solute, at the start of the reaction.
     * @param initialConcentration
     */
    public double getInitialConcentration() {
        return initialConcentration;
    }

    /**
     * Gets the pH of the solutoion at equilibrium.
     * @return
     */
    public double getPH() {
        return -MathUtil.log10( getH3OConcentration() );
    }

    /**
     * Gets the reactant concentration at equilibrium.
     * @return
     */
    public abstract double getReactantConcentration();

    /**
     * Gets the product concentration at equilibrium.
     * @return
     */
    public abstract double getProductConcentration();

    /**
     * Gets the H3O+ (hydronium) concentration at equilibrium.
     * @return
     */
    public abstract double getH3OConcentration();

    /**
     * Gets the OH- (hydroxide) concentration at equilibrium.
     * @return
     */
    public abstract double getOHConcentration();

    /**
     * Gets the H2O (water) concentration at equilibrium.
     * @return
     */
    public abstract double getH2OConcentration();

    /**
     * Gets the reactant molecule count at equilibrium.
     * @return
     */
    public double getReactantMoleculeCount() {
        return getMoleculeCount( getReactantConcentration() );
    }

    /**
     * Gets the product molecule count at equilibrium.
     * @return
     */
    public double getProductMoleculeCount() {
        return getMoleculeCount( getProductConcentration() );
    }

    /**
     * Gets the H3O+ (hydronium) molecule count at equilibrium.
     * @return
     */
    public double getH3OMoleculeCount() {
        return getMoleculeCount( getH3OConcentration() );
    }

    /**
     * Gets the OH- (hydroxide) molecule count at equilibrium.
     * @return
     */
    public double getOHMoleculeCount() {
        return getMoleculeCount( getOHConcentration() );
    }

    /**
     * Gets the H2O (water) molecule count at equilibrium.
     * @return
     */
    public double getH2OMoleculeCount() {
        return getMoleculeCount( getH2OConcentration() );
    }

    /*
     * Gets the molecule count for some specified concentration.
     */
    private static double getMoleculeCount( double concentration ) {
        return concentration * ABSConstants.AVOGADROS_NUMBER;
    }
    
    protected static double getWaterConcentration() {
        return W;
    }
    
    protected static double getWaterEquilibriumConstant() {
        return Kw;
    }
    
    public interface AqueousSolutionChangeListener extends EventListener {
        // The initial concentration of the solute has changed.
        public void initialConcentrationChanged();
    }
    
    public void addAqueousSolutionChangeListenerChangeListener( AqueousSolutionChangeListener listener ) {
        listeners.add( AqueousSolutionChangeListener.class, listener );
    }
    
    public void removeAqueousSolutionChangeListenerChangeListener( AqueousSolutionChangeListener listener ) {
        listeners.remove( AqueousSolutionChangeListener.class, listener );
    }
    
    private void fireInitialConcentrationChanged() {
        for ( AqueousSolutionChangeListener listener : listeners.getListeners( AqueousSolutionChangeListener.class ) ) {
            listener.initialConcentrationChanged();
        }
    }
}
